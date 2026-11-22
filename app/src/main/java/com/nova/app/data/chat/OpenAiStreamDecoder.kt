package com.nova.app.data.chat

import org.json.JSONObject

class OpenAiStreamDecoder {

    private var sequence = 0
    private var textId: String? = null
    private var reasoningId: String? = null
    private val openToolIds = linkedSetOf<String>()
    private var lastToolId: String? = null
    private var toolIdsByIndex = mutableMapOf<Int, String>()

    fun accept(payload: JSONObject): List<StreamChunk> {
        val out = mutableListOf<StreamChunk>()

        payload.obj("error")?.let { error ->
            throw ChatException(
                ChatError.Http(400, error.str("message").ifBlank { "provider error" }),
            )
        }

        payload.obj("usage")?.let { usage ->
            val prompt = usage.optInt("prompt_tokens", 0)
            val completion = usage.optInt("completion_tokens", 0)
            val cached = usage.obj("prompt_tokens_details")?.optInt("cached_tokens", 0) ?: 0
            if (prompt > 0 || completion > 0) {
                out.add(
                    StreamChunk.Usage(
                        Usage(
                            promptTokens = prompt,
                            completionTokens = completion,
                            cachedTokens = cached,
                            totalTokens = prompt + completion,
                        ),
                    ),
                )
            }
        }

        val choices = payload.optJSONArray("choices") ?: return out
        if (choices.length() == 0) return out
        val choice = choices.optJSONObject(0) ?: return out
        val message = choice.obj("delta") ?: choice.obj("message")

        if (message != null) {
            val reasoning = message.str("reasoning_content").ifBlank { message.str("reasoning") }
            if (reasoning.isNotEmpty()) {
                out.addAll(closeText())
                out.addAll(closeTools())
                val id = reasoningId ?: nextId("reasoning").also {
                    reasoningId = it
                    out.add(StreamChunk.ReasoningStart(it))
                }
                out.add(StreamChunk.ReasoningDelta(id, reasoning))
            }

            val content = message.str("content")
            if (content.isNotEmpty()) {
                out.addAll(closeReasoning())
                out.addAll(closeTools())
                val id = textId ?: nextId("text").also {
                    textId = it
                    out.add(StreamChunk.TextStart(it))
                }
                out.add(StreamChunk.TextDelta(id, content))
            }

            message.optJSONArray("tool_calls")?.let { calls ->
                for (i in 0 until calls.length()) {
                    val call = calls.optJSONObject(i) ?: continue
                    val index = call.optInt("index", i)
                    val rawId = call.str("id")
                    val toolId = if (rawId.isNotBlank()) {
                        toolIdsByIndex[index] = rawId
                        rawId
                    } else {
                        toolIdsByIndex.getOrPut(index) { nextId("tool") }
                    }
                    val function = call.obj("function")
                    val name = function?.str("name").orEmpty()
                    val args = function?.str("arguments").orEmpty()

                    out.addAll(closeText())
                    out.addAll(closeReasoning())

                    if (openToolIds.add(toolId)) {
                        out.add(StreamChunk.ToolCallStart(toolId, name))
                    }
                    lastToolId = toolId
                    if (args.isNotEmpty()) {
                        out.add(StreamChunk.ToolCallDelta(toolId, "", args))
                    }
                }
            }
        }

        val finish = choice.str("finish_reason")
        if (finish.isNotEmpty()) {
            out.addAll(closeText())
            out.addAll(closeReasoning())
            out.addAll(closeTools())
            out.add(StreamChunk.Finish(finish))
        }
        return out
    }

    fun finish(): List<StreamChunk> {
        val out = mutableListOf<StreamChunk>()
        out.addAll(closeText())
        out.addAll(closeReasoning())
        out.addAll(closeTools())
        out.add(StreamChunk.Finish(null))
        return out
    }

    private fun closeText(): List<StreamChunk> {
        val id = textId ?: return emptyList()
        textId = null
        return listOf(StreamChunk.TextEnd(id))
    }

    private fun closeReasoning(): List<StreamChunk> {
        val id = reasoningId ?: return emptyList()
        reasoningId = null
        return listOf(StreamChunk.ReasoningEnd(id))
    }

    private fun closeTools(): List<StreamChunk> {
        if (openToolIds.isEmpty()) return emptyList()
        val out = openToolIds.map { StreamChunk.ToolCallEnd(it) }
        openToolIds.clear()
        lastToolId = null
        return out
    }

    private fun nextId(kind: String): String {
        sequence++
        return "$kind-$sequence"
    }
}
