package com.nova.app.data.chat

class StreamChunkHandler {

    private val textPartIndexes = mutableMapOf<String, Int>()
    private val reasoningPartIndexes = mutableMapOf<String, Int>()

    fun handle(messages: List<UIMessage>, chunk: StreamChunk): List<UIMessage> {
        require(messages.isNotEmpty()) { "messages must not be empty" }

        val target = if (messages.last().role != MessageRole.ASSISTANT) {
            messages + UIMessage(role = MessageRole.ASSISTANT, parts = emptyList())
        } else {
            messages
        }
        val updated = append(target.last(), chunk)
        return target.dropLast(1) + updated
    }

    private fun append(message: UIMessage, chunk: StreamChunk): UIMessage {
        val parts = message.parts
        return when (chunk) {
            is StreamChunk.TextStart -> {
                if (chunk.id in textPartIndexes) {
                    message
                } else {
                    textPartIndexes[chunk.id] = parts.size
                    message.copy(parts = parts + MessagePart.Text(""))
                }
            }

            is StreamChunk.TextDelta -> {
                val index = textPartIndexes[chunk.id]
                if (index == null || parts.getOrNull(index) !is MessagePart.Text) {
                    textPartIndexes[chunk.id] = parts.size
                    message.copy(parts = parts + MessagePart.Text(chunk.text))
                } else {
                    val copy = parts.toMutableList()
                    val target = copy[index] as MessagePart.Text
                    copy[index] = target.copy(text = target.text + chunk.text)
                    message.copy(parts = copy)
                }
            }

            is StreamChunk.TextEnd -> {
                textPartIndexes.remove(chunk.id)
                message
            }

            is StreamChunk.ReasoningStart -> {
                if (chunk.id in reasoningPartIndexes) {
                    message
                } else {
                    reasoningPartIndexes[chunk.id] = parts.size
                    message.copy(parts = parts + MessagePart.Reasoning("", finished = false))
                }
            }

            is StreamChunk.ReasoningDelta -> {
                val index = reasoningPartIndexes[chunk.id]
                if (index == null || parts.getOrNull(index) !is MessagePart.Reasoning) {
                    reasoningPartIndexes[chunk.id] = parts.size
                    message.copy(parts = parts + MessagePart.Reasoning(chunk.text, finished = false))
                } else {
                    val copy = parts.toMutableList()
                    val target = copy[index] as MessagePart.Reasoning
                    copy[index] = target.copy(text = target.text + chunk.text)
                    message.copy(parts = copy)
                }
            }

            is StreamChunk.ReasoningEnd -> {
                val index = reasoningPartIndexes.remove(chunk.id)
                if (index == null || parts.getOrNull(index) !is MessagePart.Reasoning) {
                    message
                } else {
                    val copy = parts.toMutableList()
                    val target = copy[index] as MessagePart.Reasoning
                    copy[index] = target.copy(finished = true)
                    message.copy(parts = copy)
                }
            }

            is StreamChunk.ToolCallStart -> {
                if (parts.any { it is MessagePart.Tool && it.toolCallId == chunk.id }) {
                    message
                } else {
                    message.copy(
                        parts = parts + MessagePart.Tool(
                            toolCallId = chunk.id,
                            toolName = chunk.toolName,
                            input = "",
                        ),
                    )
                }
            }

            is StreamChunk.ToolCallDelta -> {
                val copy = parts.map { part ->
                    if (part is MessagePart.Tool && part.toolCallId == chunk.id) {
                        part.copy(
                            toolName = part.toolName + chunk.toolNameDelta,
                            input = part.input + chunk.inputDelta,
                        )
                    } else {
                        part
                    }
                }
                message.copy(parts = copy)
            }

            is StreamChunk.ToolCallEnd -> message

            is StreamChunk.Usage -> message.copy(usage = message.usage.merge(chunk.usage))

            is StreamChunk.Finish -> {
                val finished = message.finishReasoning()
                textPartIndexes.clear()
                reasoningPartIndexes.clear()
                finished
            }
        }
    }

    fun reset() {
        textPartIndexes.clear()
        reasoningPartIndexes.clear()
    }
}
