package com.nova.app.data.chat

import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader

class OpenAiStream {

    companion object {
        private const val DONE = "[DONE]"
        private const val DATA = "data:"
        const val TEXT_ID = "text"
        const val REASONING_ID = "reasoning"
    }

    fun read(reader: BufferedReader, onChunk: (ChatChunk) -> Unit) {
        while (true) {
            val line = reader.readLine() ?: break
            if (line.isBlank()) continue
            if (!line.startsWith(DATA)) continue
            val payload = line.removePrefix(DATA).trim()
            if (payload == DONE) {
                onChunk(ChatChunk.ReasoningEnd(REASONING_ID))
                onChunk(ChatChunk.TextEnd(TEXT_ID))
                onChunk(ChatChunk.Done)
                break
            }
            parse(payload, onChunk)
        }
        onChunk(ChatChunk.ReasoningEnd(REASONING_ID))
        onChunk(ChatChunk.TextEnd(TEXT_ID))
    }

    private fun parse(raw: String, onChunk: (ChatChunk) -> Unit) {
        runCatching {
            val root = JSONObject(raw)

            root.obj("usage")?.let { usage ->
                val prompt = usage.optInt("prompt_tokens", 0)
                val completion = usage.optInt("completion_tokens", 0)
                if (prompt > 0 || completion > 0) {
                    onChunk(ChatChunk.Usage(prompt, completion))
                }
            }

            val choices: JSONArray = root.optJSONArray("choices") ?: return
            if (choices.length() == 0) return
            val first = choices.optJSONObject(0) ?: return

            val delta = first.obj("delta")
            if (delta != null) {
                val reasoning = delta.str("reasoning_content").ifBlank { delta.str("reasoning") }
                if (reasoning.isNotEmpty()) {
                    onChunk(ChatChunk.ReasoningStart(REASONING_ID))
                    onChunk(ChatChunk.ReasoningDelta(REASONING_ID, reasoning))
                }

                val content = delta.str("content")
                if (content.isNotEmpty()) {
                    onChunk(ChatChunk.TextStart(TEXT_ID))
                    onChunk(ChatChunk.TextDelta(TEXT_ID, content))
                }

                val toolCalls = delta.optJSONArray("tool_calls")
                if (toolCalls != null && toolCalls.length() > 0) {
                    for (i in 0 until toolCalls.length()) {
                        val call = toolCalls.optJSONObject(i) ?: continue
                        val index = call.optInt("index", i)
                        val id = call.str("id")
                        val function = call.obj("function")
                        val name = function?.str("name").orEmpty()
                        val args = function?.str("arguments").orEmpty()
                        onChunk(ChatChunk.ToolCallDelta(index, id, name, args))
                    }
                }
            }

            val finish = first.str("finish_reason")
            if (finish.isNotEmpty()) {
                onChunk(ChatChunk.Finish(finish))
            }
        }.getOrNull()
    }
}
