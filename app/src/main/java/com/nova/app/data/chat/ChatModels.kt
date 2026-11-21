package com.nova.app.data.chat

import com.nova.app.data.provider.Provider

sealed interface ChatChunk {
    data class Delta(val text: String) : ChatChunk
    data class ReasoningDelta(val text: String) : ChatChunk
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

data class ChatTurn(
    val role: String,
    val content: String,
    val name: String = "",
    val toolCallId: String = "",
    val reasoning: String = "",
    val toolCalls: List<ToolCallRecord> = emptyList(),
)

data class ToolCallRecord(
    val name: String,
    val arguments: String,
    val result: String = "",
    val running: Boolean = false,
)

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
