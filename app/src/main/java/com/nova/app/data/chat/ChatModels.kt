package com.nova.app.data.chat

import com.nova.app.data.provider.Provider

sealed interface ChatChunk {
    data class TextStart(val id: String) : ChatChunk
    data class TextDelta(val id: String, val text: String) : ChatChunk
    data class TextEnd(val id: String) : ChatChunk
    data class ReasoningStart(val id: String) : ChatChunk
    data class ReasoningDelta(val id: String, val text: String) : ChatChunk
    data class ReasoningEnd(val id: String) : ChatChunk
    data class ToolCallDelta(
        val index: Int,
        val id: String,
        val name: String,
        val arguments: String,
    ) : ChatChunk

    data class Finish(val reason: String) : ChatChunk
    data class Usage(val promptTokens: Int, val completionTokens: Int) : ChatChunk
    data object Done : ChatChunk
}

sealed interface ChatError {
    data class Http(val code: Int, val detail: String) : ChatError
    data class Network(val message: String) : ChatError
    data class InvalidResponse(val message: String) : ChatError
    data class Cancelled(val message: String) : ChatError
}

data class ChatRequest(
    val provider: Provider,
    val model: String,
    val messages: List<ChatTurn>,
    val systemPrompt: String = "",
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val tools: List<ChatTool> = emptyList(),
    val reasoning: String? = null,
    val stream: Boolean = true,
)

sealed interface ChatPart {
    data class Text(val text: String) : ChatPart
    data class Reasoning(
        val text: String,
        val finished: Boolean = false,
    ) : ChatPart

    data class Tool(
        val toolCallId: String,
        val name: String,
        val arguments: String,
        val output: String = "",
        val isExecuted: Boolean = false,
    ) : ChatPart
}

data class ChatTurn(
    val role: String,
    val content: String = "",
    val name: String = "",
    val toolCallId: String = "",
    val parts: List<ChatPart> = emptyList(),
) {
    fun textOnly(): String = when {
        content.isNotBlank() -> content
        else -> parts.filterIsInstance<ChatPart.Text>().joinToString("") { it.text }
    }
}

data class ChatTool(
    val name: String,
    val description: String,
    val parameters: String,
)

class ChatException(val error: ChatError) : Exception(
    when (error) {
        is ChatError.Http -> "HTTP ${error.code}: ${error.detail}"
        is ChatError.Network -> error.message
        is ChatError.InvalidResponse -> error.message
        is ChatError.Cancelled -> error.message
    },
)
