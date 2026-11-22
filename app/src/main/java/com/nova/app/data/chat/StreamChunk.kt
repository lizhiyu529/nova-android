package com.nova.app.data.chat

sealed interface StreamChunk {
    data class TextStart(val id: String) : StreamChunk
    data class TextDelta(val id: String, val text: String) : StreamChunk
    data class TextEnd(val id: String) : StreamChunk

    data class ReasoningStart(val id: String) : StreamChunk
    data class ReasoningDelta(val id: String, val text: String) : StreamChunk
    data class ReasoningEnd(val id: String) : StreamChunk

    data class ToolCallStart(val id: String, val toolName: String = "") : StreamChunk
    data class ToolCallDelta(
        val id: String,
        val toolNameDelta: String = "",
        val inputDelta: String = "",
    ) : StreamChunk

    data class ToolCallEnd(val id: String) : StreamChunk

    data class Usage(val usage: com.nova.app.data.chat.Usage) : StreamChunk
    data class Finish(val finishReason: String? = null) : StreamChunk
}
