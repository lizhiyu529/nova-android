package com.nova.app.data.chat

import org.json.JSONArray
import org.json.JSONObject

object OpenAiRequest {

    fun build(
        model: String,
        messages: List<UIMessage>,
        systemPrompt: String,
        tools: List<ChatTool>,
        temperature: Double?,
        maxTokens: Int?,
        reasoning: String?,
        stream: Boolean,
    ): JSONObject {
        return JSONObject().apply {
            put("model", model)
            put("messages", buildMessages(messages, systemPrompt))
            put("stream", stream)
            temperature?.let { put("temperature", it) }
            maxTokens?.let { put("max_tokens", it) }
            reasoning?.let { put("reasoning_effort", it) }
            if (stream) {
                put("stream_options", JSONObject().put("include_usage", true))
            }
            if (tools.isNotEmpty()) {
                put("tools", buildTools(tools))
            }
        }
    }

    private fun buildTools(tools: List<ChatTool>): JSONArray = JSONArray().apply {
        tools.forEach { tool ->
            put(
                JSONObject().apply {
                    put("type", "function")
                    put(
                        "function",
                        JSONObject().apply {
                            put("name", tool.name)
                            put("description", tool.description)
                            put("parameters", JSONObject(tool.parameters))
                        },
                    )
                },
            )
        }
    }

    fun buildMessages(messages: List<UIMessage>, systemPrompt: String): JSONArray {
        val out = JSONArray()
        if (systemPrompt.isNotBlank()) {
            out.put(
                JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                },
            )
        }
        messages.filter { it.isValidToUpload() }.forEach { message ->
            if (message.role == MessageRole.ASSISTANT) {
                addAssistant(out, message)
            } else {
                out.put(buildPlain(message))
            }
        }
        return out
    }

    private fun buildPlain(message: UIMessage): JSONObject = JSONObject().apply {
        put("role", message.role.value)
        put("content", message.toText())
    }

    private fun addAssistant(out: JSONArray, message: UIMessage) {
        val groups = groupPartsByToolBoundary(message.parts)
        val contentBuffer = mutableListOf<MessagePart>()
        var reasoning = ""

        groups.forEach { group ->
            when (group) {
                is PartGroup.Content -> {
                    group.parts.filterIsInstance<MessagePart.Reasoning>().forEach { part ->
                        if (part.text.isNotBlank()) reasoning = part.text
                    }
                    group.parts.filterIsInstance<MessagePart.Text>().forEach { contentBuffer.add(it) }
                }

                is PartGroup.Tools -> {
                    buildAssistant(contentBuffer, group.tools, reasoning)?.let { out.put(it) }
                    contentBuffer.clear()
                    reasoning = ""
                    group.tools.forEach { tool ->
                        out.put(
                            JSONObject().apply {
                                put("role", "tool")
                                put("tool_call_id", tool.toolCallId)
                                put("content", tool.output.ifBlank { EMPTY_TOOL_OUTPUT })
                            },
                        )
                    }
                }
            }
        }

        if (contentBuffer.isNotEmpty() || reasoning.isNotBlank()) {
            buildAssistant(contentBuffer, emptyList(), reasoning)?.let { out.put(it) }
        }
    }

    private fun buildAssistant(
        contentParts: List<MessagePart>,
        tools: List<MessagePart.Tool>,
        reasoning: String,
    ): JSONObject? {
        val text = contentParts.filterIsInstance<MessagePart.Text>().joinToString("") { it.text }
        if (text.isBlank() && tools.isEmpty()) return null

        return JSONObject().apply {
            put("role", "assistant")
            if (tools.isEmpty()) {
                put("content", text)
                return@apply
            }
            if (reasoning.isNotBlank()) put("reasoning_content", reasoning)
            if (text.isBlank()) put("content", JSONObject.NULL) else put("content", text)
            put(
                "tool_calls",
                JSONArray().apply {
                    tools.forEach { tool ->
                        put(
                            JSONObject().apply {
                                put("id", tool.toolCallId)
                                put("type", "function")
                                put(
                                    "function",
                                    JSONObject().apply {
                                        put("name", tool.toolName)
                                        put("arguments", tool.input.ifBlank { "{}" })
                                    },
                                )
                            },
                        )
                    }
                },
            )
        }
    }

    private const val EMPTY_TOOL_OUTPUT = "(no output)"
}
