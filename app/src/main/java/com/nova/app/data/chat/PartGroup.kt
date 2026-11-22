package com.nova.app.data.chat

import com.nova.app.data.provider.Provider

sealed interface ChatError {
    data class Http(val code: Int, val detail: String) : ChatError
    data class Network(val message: String) : ChatError
    data class InvalidResponse(val message: String) : ChatError
    data class Cancelled(val message: String) : ChatError
}

class ChatException(val error: ChatError) : Exception(
    when (error) {
        is ChatError.Http -> "HTTP ${error.code}: ${error.detail}"
        is ChatError.Network -> error.message
        is ChatError.InvalidResponse -> error.message
        is ChatError.Cancelled -> error.message
    },
)

data class ChatRequest(
    val provider: Provider,
    val model: String,
    val messages: List<UIMessage>,
    val systemPrompt: String = "",
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val tools: List<ChatTool> = emptyList(),
    val reasoning: String? = null,
    val stream: Boolean = true,
)

data class ChatTool(
    val name: String,
    val description: String,
    val parameters: String,
)

sealed interface PartGroup {
    data class Content(val parts: List<MessagePart>) : PartGroup
    data class Tools(val tools: List<MessagePart.Tool>) : PartGroup
}

fun groupPartsByToolBoundary(parts: List<MessagePart>): List<PartGroup> {
    val groups = mutableListOf<PartGroup>()
    val content = mutableListOf<MessagePart>()
    val tools = mutableListOf<MessagePart.Tool>()

    fun flushContent() {
        if (content.isNotEmpty()) {
            groups.add(PartGroup.Content(content.toList()))
            content.clear()
        }
    }

    fun flushTools() {
        if (tools.isNotEmpty()) {
            groups.add(PartGroup.Tools(tools.toList()))
            tools.clear()
        }
    }

    for (part in parts) {
        if (part is MessagePart.Tool && part.isExecuted) {
            flushContent()
            tools.add(part)
        } else {
            flushTools()
            content.add(part)
        }
    }
    flushContent()
    flushTools()
    return groups
}
