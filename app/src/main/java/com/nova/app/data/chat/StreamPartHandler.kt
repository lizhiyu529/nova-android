package com.nova.app.data.chat

class StreamPartHandler {

    private val textIndexes = mutableMapOf<String, Int>()
    private val reasoningIndexes = mutableMapOf<String, Int>()

    fun reset() {
        textIndexes.clear()
        reasoningIndexes.clear()
    }

    fun handle(parts: List<ChatPart>, chunk: ChatChunk): List<ChatPart> {
        return when (chunk) {
            is ChatChunk.TextStart -> {
                if (chunk.id in textIndexes) {
                    parts
                } else {
                    val next = parts + ChatPart.Text("")
                    textIndexes[chunk.id] = parts.size
                    next
                }
            }

            is ChatChunk.TextDelta -> {
                val index = textIndexes[chunk.id]
                if (index == null || parts.getOrNull(index) !is ChatPart.Text) {
                    val next = parts + ChatPart.Text(chunk.text)
                    textIndexes[chunk.id] = parts.size
                    next
                } else {
                    parts.toMutableList().apply {
                        val target = this[index] as ChatPart.Text
                        this[index] = target.copy(text = target.text + chunk.text)
                    }
                }
            }

            is ChatChunk.TextEnd -> {
                val index = textIndexes.remove(chunk.id)
                if (index == null || parts.getOrNull(index) !is ChatPart.Text) {
                    parts
                } else {
                    parts.toMutableList().apply {
                        val target = this[index] as ChatPart.Text
                        this[index] = target.copy(finished = true)
                    }
                }
            }

            is ChatChunk.ReasoningStart -> {
                if (chunk.id in reasoningIndexes) {
                    parts
                } else {
                    val next = parts + ChatPart.Reasoning("")
                    reasoningIndexes[chunk.id] = parts.size
                    next
                }
            }

            is ChatChunk.ReasoningDelta -> {
                val index = reasoningIndexes[chunk.id]
                if (index == null || parts.getOrNull(index) !is ChatPart.Reasoning) {
                    val next = parts + ChatPart.Reasoning(chunk.text)
                    reasoningIndexes[chunk.id] = parts.size
                    next
                } else {
                    parts.toMutableList().apply {
                        val target = this[index] as ChatPart.Reasoning
                        this[index] = target.copy(text = target.text + chunk.text)
                    }
                }
            }

            is ChatChunk.ReasoningEnd -> {
                val index = reasoningIndexes.remove(chunk.id)
                if (index == null || parts.getOrNull(index) !is ChatPart.Reasoning) {
                    parts
                } else {
                    parts.toMutableList().apply {
                        val target = this[index] as ChatPart.Reasoning
                        this[index] = target.copy(finished = true)
                    }
                }
            }

            else -> parts
        }
    }

    fun upsertTool(parts: List<ChatPart>, chunk: ChatChunk.ToolCallDelta): List<ChatPart> {
        if (chunk.id.isBlank()) return parts
        val existing = parts.indexOfFirst { it is ChatPart.Tool && it.toolCallId == chunk.id }
        if (existing < 0) {
            return parts + ChatPart.Tool(
                toolCallId = chunk.id,
                name = chunk.name,
                arguments = chunk.arguments,
            )
        }
        return parts.toMutableList().apply {
            val target = this[existing] as ChatPart.Tool
            this[existing] = target.copy(
                name = if (chunk.name.isNotBlank()) target.name + chunk.name else target.name,
                arguments = target.arguments + chunk.arguments,
            )
        }
    }

    fun markToolExecuted(parts: List<ChatPart>, toolCallId: String, output: String): List<ChatPart> {
        return parts.map { part ->
            if (part is ChatPart.Tool && part.toolCallId == toolCallId) {
                part.copy(output = output, isExecuted = true)
            } else {
                part
            }
        }
    }
}

fun List<ChatPart>.isEmptyContent(): Boolean {
    if (isEmpty()) return true
    return all { part ->
        when (part) {
            is ChatPart.Text -> part.text.isBlank()
            is ChatPart.Reasoning -> part.text.isBlank()
            is ChatPart.Tool -> false
        }
    }
}

fun List<ChatPart>.visibleParts(): List<ChatPart> {
    return filter { part ->
        when (part) {
            is ChatPart.Text -> part.text.isNotBlank()
            is ChatPart.Reasoning -> part.text.isNotBlank()
            is ChatPart.Tool -> true
        }
    }
}

fun List<ChatPart>.textContent(): String {
    return filterIsInstance<ChatPart.Text>().joinToString("") { it.text }
}
