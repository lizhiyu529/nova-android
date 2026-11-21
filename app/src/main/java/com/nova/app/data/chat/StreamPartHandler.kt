package com.nova.app.data.chat

class StreamPartHandler {

    private val textIndexes = mutableMapOf<String, Int>()
    private val reasoningIndexes = mutableMapOf<String, Int>()

    fun handle(parts: List<ChatPart>, chunk: ChatChunk): List<ChatPart> {
        return when (chunk) {
            is ChatChunk.TextStart -> {
                if (chunk.id in textIndexes) {
                    parts
                } else {
                    textIndexes[chunk.id] = parts.size
                    parts + ChatPart.Text("")
                }
            }

            is ChatChunk.TextDelta -> {
                val index = textIndexes[chunk.id]
                if (index == null || parts.getOrNull(index) !is ChatPart.Text) {
                    textIndexes[chunk.id] = parts.size
                    parts + ChatPart.Text(chunk.text)
                } else {
                    parts.toMutableList().apply {
                        val target = this[index] as ChatPart.Text
                        this[index] = target.copy(text = target.text + chunk.text)
                    }
                }
            }

            is ChatChunk.TextEnd -> {
                textIndexes.remove(chunk.id)
                parts
            }

            is ChatChunk.ReasoningStart -> {
                if (chunk.id in reasoningIndexes) {
                    parts
                } else {
                    reasoningIndexes[chunk.id] = parts.size
                    parts + ChatPart.Reasoning("", finished = false)
                }
            }

            is ChatChunk.ReasoningDelta -> {
                val index = reasoningIndexes[chunk.id]
                if (index == null || parts.getOrNull(index) !is ChatPart.Reasoning) {
                    reasoningIndexes[chunk.id] = parts.size
                    parts + ChatPart.Reasoning(chunk.text, finished = false)
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

            is ChatChunk.ToolCallStart -> {
                if (parts.any { it is ChatPart.Tool && it.toolCallId == chunk.id }) {
                    parts
                } else {
                    parts + ChatPart.Tool(
                        toolCallId = chunk.id,
                        toolName = chunk.toolName,
                        input = "",
                    )
                }
            }

            is ChatChunk.ToolCallDelta -> parts.map { part ->
                if (part is ChatPart.Tool && part.toolCallId == chunk.id) {
                    part.copy(
                        toolName = part.toolName + chunk.toolNameDelta,
                        input = part.input + chunk.inputDelta,
                    )
                } else {
                    part
                }
            }

            is ChatChunk.ToolCallEnd -> parts

            else -> parts
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
