package com.nova.app.data.chat

import java.io.BufferedReader
import org.json.JSONObject

class OpenAiStream {

    private val state = StreamState()
    private var finished = false

    fun read(reader: BufferedReader, onChunk: (ChatChunk) -> Unit) {
        while (true) {
            val line = reader.readLine() ?: break
            if (line.isBlank()) continue
            if (!line.startsWith(DATA)) continue
            val payload = line.removePrefix(DATA).trim()
            if (payload == DONE) {
                state.finish().forEach(onChunk)
                finished = true
                break
            }
            accept(payload, onChunk)
        }
        if (!finished) {
            state.finish().forEach(onChunk)
        }
    }

    private fun accept(raw: String, onChunk: (ChatChunk) -> Unit) {
        runCatching {
            val payload = JSONObject(raw)
            payload.obj("error")?.let { error ->
                throw ChatException(
                    ChatError.Http(
                        400,
                        error.str("message").ifBlank { "provider error" },
                    ),
                )
            }

            payload.obj("usage")?.let { usage ->
                val prompt = usage.optInt("prompt_tokens", 0)
                val completion = usage.optInt("completion_tokens", 0)
                val total = usage.optInt("total_tokens", prompt + completion)
                if (prompt > 0 || completion > 0) {
                    onChunk(ChatChunk.Usage(prompt, completion, total))
                }
            }

            val choices = payload.optJSONArray("choices") ?: return
            if (choices.length() == 0) return
            val choice = choices.optJSONObject(0) ?: return

            val message = choice.obj("delta") ?: choice.obj("message")
            if (message != null) {
                val reasoning = message.str("reasoning_content")
                    .ifBlank { message.str("reasoning") }
                if (reasoning.isNotEmpty()) {
                    state.appendReasoning(reasoning).forEach(onChunk)
                }

                val content = message.str("content")
                if (content.isNotEmpty()) {
                    state.appendText(content).forEach(onChunk)
                }

                val toolCalls = message.optJSONArray("tool_calls")
                if (toolCalls != null && toolCalls.length() > 0) {
                    for (i in 0 until toolCalls.length()) {
                        val call = toolCalls.optJSONObject(i) ?: continue
                        val id = call.str("id")
                        val function = call.obj("function")
                        val name = function?.str("name").orEmpty()
                        val args = function?.str("arguments").orEmpty()
                        state.appendTool(id, name, args).forEach(onChunk)
                    }
                }
            }

            val finishReason = choice.str("finish_reason")
            if (finishReason.isNotEmpty()) {
                state.finish().forEach(onChunk)
                finished = true
            }
        }.onFailure { throwable ->
            if (throwable is ChatException) throw throwable
        }
    }

    companion object {
        private const val DONE = "[DONE]"
        private const val DATA = "data:"
    }
}
