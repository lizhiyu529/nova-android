package com.nova.app.data.chat

import java.io.BufferedReader
import org.json.JSONArray
import org.json.JSONObject

object OpenAiStream {

    private const val DONE = "[DONE]"
    private const val DATA = "data:"

    fun read(reader: BufferedReader, onChunk: (ChatChunk) -> Unit) {
        while (true) {
            val line = reader.readLine() ?: break
            if (line.isBlank()) continue
            if (!line.startsWith(DATA)) continue
            val payload = line.removePrefix(DATA).trim()
            if (payload == DONE) {
                onChunk(ChatChunk.Done)
                break
            }
            parseChunk(payload)?.let(onChunk)
        }
    }

    fun parseChunk(raw: String): ChatChunk? {
        return runCatching {
            val root = JSONObject(raw)

            val usage = root.optJSONObject("usage")
            if (usage != null) {
                val prompt = usage.optInt("prompt_tokens", 0)
                val completion = usage.optInt("completion_tokens", 0)
                if (prompt > 0 || completion > 0) {
                    return ChatChunk.Usage(prompt, completion)
                }
            }

            val choices: JSONArray = root.optJSONArray("choices") ?: return null
            if (choices.length() == 0) return null
            val first = choices.optJSONObject(0) ?: return null

            val finish = first.optString("finish_reason", "")
            val delta = first.optJSONObject("delta")

            if (delta != null) {
                val reasoning = delta.optString("reasoning_content", "")
                    .ifBlank { delta.optString("reasoning", "") }
                if (reasoning.isNotEmpty()) return ChatChunk.ReasoningDelta(reasoning)

                val content = delta.optString("content", "")
                if (content.isNotEmpty()) return ChatChunk.Delta(content)

                val toolCalls = delta.optJSONArray("tool_calls")
                if (toolCalls != null && toolCalls.length() > 0) {
                    val call = toolCalls.optJSONObject(0)
                    if (call != null) {
                        val index = call.optInt("index", 0)
                        val id = call.optString("id", "")
                        val function = call.optJSONObject("function")
                        val name = function?.optString("name", "").orEmpty()
                        val args = function?.optString("arguments", "").orEmpty()
                        return ChatChunk.ToolCallDelta(index, id, name, args)
                    }
                }
            }

            if (finish.isNotEmpty()) {
                return ChatChunk.Finish(finish)
            }
            null
        }.getOrNull()
    }
}
