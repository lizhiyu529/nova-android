package com.nova.app.data.chat

import org.json.JSONArray
import org.json.JSONObject

object MessageJson {

    fun encode(message: UIMessage): String {
        val parts = JSONArray()
        message.parts.forEach { part ->
            parts.put(
                when (part) {
                    is MessagePart.Text -> JSONObject().apply {
                        put("type", TYPE_TEXT)
                        put("text", part.text)
                    }

                    is MessagePart.Reasoning -> JSONObject().apply {
                        put("type", TYPE_REASONING)
                        put("text", part.text)
                        put("finished", part.finished)
                    }

                    is MessagePart.Tool -> JSONObject().apply {
                        put("type", TYPE_TOOL)
                        put("toolCallId", part.toolCallId)
                        put("toolName", part.toolName)
                        put("input", part.input)
                        put("output", part.output)
                        put("isExecuted", part.isExecuted)
                    }
                },
            )
        }
        return JSONObject().apply {
            put("role", message.role.value)
            put("parts", parts)
            put("finished", message.finished)
            message.usage?.let { usage ->
                put(
                    "usage",
                    JSONObject().apply {
                        put("prompt", usage.promptTokens)
                        put("completion", usage.completionTokens)
                        put("cached", usage.cachedTokens)
                        put("total", usage.totalTokens)
                    },
                )
            }
        }.toString()
    }

    fun decodeParts(raw: String): List<MessagePart> {
        if (raw.isBlank()) return emptyList()
        val array = runCatching { JSONArray(raw) }.getOrNull() ?: return emptyList()
        val out = mutableListOf<MessagePart>()
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            out.add(
                when (item.str("type")) {
                    TYPE_TEXT -> MessagePart.Text(item.str("text"))
                    TYPE_REASONING -> MessagePart.Reasoning(
                        text = item.str("text"),
                        finished = item.optBoolean("finished", true),
                    )

                    TYPE_TOOL -> MessagePart.Tool(
                        toolCallId = item.str("toolCallId"),
                        toolName = item.str("toolName"),
                        input = item.str("input"),
                        output = item.str("output"),
                        isExecuted = item.optBoolean("isExecuted", false),
                    )

                    else -> continue
                },
            )
        }
        return out
    }

    fun decodeRole(raw: String): MessageRole =
        MessageRole.fromValueOrNull(raw) ?: MessageRole.USER

    fun decodeUsage(raw: String): Usage? {
        if (raw.isBlank()) return null
        val obj = runCatching { JSONObject(raw) }.getOrNull() ?: return null
        val prompt = obj.optInt("prompt", 0)
        val completion = obj.optInt("completion", 0)
        if (prompt == 0 && completion == 0) return null
        return Usage(
            promptTokens = prompt,
            completionTokens = completion,
            cachedTokens = obj.optInt("cached", 0),
            totalTokens = obj.optInt("total", prompt + completion),
        )
    }

    fun usageToJson(usage: Usage?): String {
        if (usage == null) return ""
        return JSONObject().apply {
            put("prompt", usage.promptTokens)
            put("completion", usage.completionTokens)
            put("cached", usage.cachedTokens)
            put("total", usage.totalTokens)
        }.toString()
    }

    private const val TYPE_TEXT = "text"
    private const val TYPE_REASONING = "reasoning"
    private const val TYPE_TOOL = "tool"
}
