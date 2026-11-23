package com.nova.app.data.chat

import java.util.UUID

enum class MessageRole(val value: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    TOOL("tool"),
    ;

    companion object {
        fun fromValueOrNull(value: String?): MessageRole? =
            entries.firstOrNull { it.value.equals(value, ignoreCase = true) }
    }
}

sealed interface MessagePart {
    data class Text(val text: String) : MessagePart
    data class Reasoning(
        val text: String,
        val finished: Boolean = false,
    ) : MessagePart

    data class Tool(
        val toolCallId: String,
        val toolName: String,
        val input: String,
        val output: String = "",
        val isExecuted: Boolean = false,
    ) : MessagePart
}

data class Usage(
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val cachedTokens: Int = 0,
    val totalTokens: Int = 0,
)

fun Usage?.merge(other: Usage): Usage {
    val prompt = if (other.promptTokens > 0) other.promptTokens else this?.promptTokens ?: 0
    val completion = if (other.completionTokens > 0) other.completionTokens else this?.completionTokens ?: 0
    val cached = if (other.cachedTokens > 0) other.cachedTokens else this?.cachedTokens ?: 0
    return Usage(
        promptTokens = prompt,
        completionTokens = completion,
        cachedTokens = cached,
        totalTokens = prompt + completion,
    )
}

fun Usage?.sum(other: Usage?): Usage? {
    if (this == null) return other
    if (other == null) return this
    return Usage(
        promptTokens = promptTokens + other.promptTokens,
        completionTokens = completionTokens + other.completionTokens,
        cachedTokens = cachedTokens + other.cachedTokens,
        totalTokens = totalTokens + other.totalTokens,
    )
}

data class UIMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val parts: List<MessagePart> = emptyList(),
    val usage: Usage? = null,
    val lastUsage: Usage? = null,
    val finished: Boolean = false,
    val steps: Int = 0,
    val elapsedMs: Long = 0L,
) {
    fun getTools(): List<MessagePart.Tool> = parts.filterIsInstance<MessagePart.Tool>()

    fun toText(): String = parts.filterIsInstance<MessagePart.Text>().joinToString("") { it.text }

    fun reasoningText(): String =
        parts.filterIsInstance<MessagePart.Reasoning>().joinToString("") { it.text }

    fun isValidToUpload(): Boolean {
        if (role == MessageRole.TOOL) return true
        if (parts.isEmpty()) return false
        return parts.any { part ->
            when (part) {
                is MessagePart.Text -> part.text.isNotBlank()
                is MessagePart.Reasoning -> part.text.isNotBlank()
                is MessagePart.Tool -> true
            }
        }
    }

    fun finishReasoning(): UIMessage = copy(
        parts = parts.map { part ->
            if (part is MessagePart.Reasoning && !part.finished) {
                part.copy(finished = true)
            } else {
                part
            }
        },
        finished = true,
    )

    fun finishPendingTools(transform: (MessagePart.Tool) -> MessagePart.Tool): UIMessage = copy(
        parts = parts.map { part ->
            if (part is MessagePart.Tool && !part.isExecuted) transform(part) else part
        },
    )

    fun appendParts(extra: List<MessagePart>): UIMessage = copy(
        parts = parts + extra,
        steps = steps + 1,
        finished = true,
    ).finishReasoning()
}

fun List<MessagePart>.isEmptyUIMessage(): Boolean {
    if (isEmpty()) return true
    return all { part ->
        when (part) {
            is MessagePart.Text -> part.text.isBlank()
            is MessagePart.Reasoning -> part.text.isBlank()
            is MessagePart.Tool -> false
        }
    }
}
