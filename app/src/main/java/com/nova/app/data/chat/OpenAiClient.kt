package com.nova.app.data.chat

import com.nova.app.data.provider.chatUrl
import com.nova.app.data.provider.normalizeEndpoint
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

object OpenAiClient {

    private const val DONE = "[DONE]"
    private const val DATA = "data:"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun describe(throwable: Throwable): ChatError = when (throwable) {
        is UnknownHostException -> ChatError.Network("DNS 解析失败：找不到主机")
        is SocketTimeoutException -> ChatError.Network("请求超时，请检查网络或端点")
        is IOException -> ChatError.Network(throwable.message?.take(160) ?: "网络异常")
        else -> ChatError.Network(throwable.message?.take(160) ?: throwable.javaClass.simpleName)
    }

    private fun buildRequest(request: ChatRequest, stream: Boolean): Request {
        val endpoint = normalizeEndpoint(request.provider.endpoint, request.provider.allowHttp)
        val body = OpenAiRequest.build(
            model = request.model,
            messages = request.messages,
            systemPrompt = request.systemPrompt,
            tools = request.tools,
            temperature = request.temperature,
            maxTokens = request.maxTokens,
            reasoning = request.reasoning,
            stream = stream,
        ).toString()

        val builder = Request.Builder()
            .url(chatUrl(endpoint))
            .post(body.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))
            .header("Accept", if (stream) "text/event-stream" else "application/json")
        if (request.provider.apiKey.isNotBlank()) {
            builder.header("Authorization", "Bearer ${request.provider.apiKey.trim()}")
        }
        return builder.build()
    }

    private fun httpError(code: Int, body: String): ChatError.Http {
        val detail = runCatching {
            val json = JSONObject(body)
            val error = json.optJSONObject("error")
            error?.str("message").orEmpty().ifBlank { json.str("message") }
        }.getOrDefault("")
        return ChatError.Http(code, detail.ifBlank { body.take(200) })
    }

    fun stream(request: ChatRequest): Flow<StreamChunk> = channelFlow {
        val call = client.newCall(buildRequest(request, true))
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                close(ChatException(describe(e)))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    val source = res.body
                    if (source == null) {
                        close(ChatException(ChatError.InvalidResponse("响应为空")))
                        return
                    }
                    if (!res.isSuccessful) {
                        close(ChatException(httpError(res.code, source.string())))
                        return
                    }
                    val decoder = OpenAiStreamDecoder()
                    try {
                        val reader = source.charStream().buffered()
                        val dataLines = mutableListOf<String>()
                        fun dispatch(raw: String) {
                            if (raw == DONE) {
                                decoder.finish().forEach { trySend(it) }
                                return
                            }
                            raw.split("\n").forEach { line ->
                                val payload = line.trim()
                                if (payload.isEmpty()) return@forEach
                                val json = runCatching { JSONObject(payload) }.getOrNull() ?: return@forEach
                                decoder.accept(json).forEach { trySend(it) }
                            }
                        }
                        while (true) {
                            val line = reader.readLine() ?: break
                            if (line.isEmpty()) {
                                if (dataLines.isNotEmpty()) {
                                    dispatch(dataLines.joinToString("\n"))
                                    dataLines.clear()
                                }
                                continue
                            }
                            if (line.startsWith(":")) continue
                            if (!line.startsWith(DATA)) continue
                            dataLines.add(line.removePrefix(DATA).removePrefix(" ").trim())
                        }
                        if (dataLines.isNotEmpty()) {
                            dispatch(dataLines.joinToString("\n"))
                        }
                        decoder.finish().forEach { trySend(it) }
                        close()
                    } catch (e: Exception) {
                        close(if (e is ChatException) e else ChatException(describe(e)))
                    }
                }
            }
        })
        awaitClose { call.cancel() }
    }.buffer(Channel.UNLIMITED).flowOn(Dispatchers.IO)

    fun generate(request: ChatRequest): Flow<UIMessage> = flow {
        val response = withContext(Dispatchers.IO) {
            runCatching { client.newCall(buildRequest(request, false)).execute() }
                .getOrElse { throw ChatException(describe(it)) }
        }
        response.use { res ->
            val body = res.body?.string().orEmpty()
            if (!res.isSuccessful) throw ChatException(httpError(res.code, body))
            val root = runCatching { JSONObject(body) }.getOrElse {
                throw ChatException(ChatError.InvalidResponse(it.message ?: "解析失败"))
            }
            root.obj("error")?.let { error ->
                throw ChatException(ChatError.Http(400, error.str("message").ifBlank { "provider error" }))
            }
            val choices = root.optJSONArray("choices")
                ?: throw ChatException(ChatError.InvalidResponse("缺少 choices"))
            val first = choices.optJSONObject(0)
                ?: throw ChatException(ChatError.InvalidResponse("choices 为空"))
            val message = first.obj("message")
                ?: throw ChatException(ChatError.InvalidResponse("缺少 message"))

            val parts = mutableListOf<MessagePart>()
            val reasoning = message.str("reasoning_content").ifBlank { message.str("reasoning") }
            if (reasoning.isNotBlank()) {
                parts.add(MessagePart.Reasoning(reasoning, finished = true))
            }
            message.str("content").takeIf { it.isNotBlank() }?.let {
                parts.add(MessagePart.Text(it))
            }
            message.optJSONArray("tool_calls")?.let { calls ->
                for (i in 0 until calls.length()) {
                    val call = calls.optJSONObject(i) ?: continue
                    val function = call.obj("function")
                    parts.add(
                        MessagePart.Tool(
                            toolCallId = call.str("id").ifBlank { "tool-$i" },
                            toolName = function?.str("name").orEmpty(),
                            input = function?.str("arguments").orEmpty(),
                        ),
                    )
                }
            }

            val usage = root.obj("usage")?.let {
                val prompt = it.optInt("prompt_tokens", 0)
                val completion = it.optInt("completion_tokens", 0)
                val cached = it.obj("prompt_tokens_details")?.optInt("cached_tokens", 0) ?: 0
                Usage(
                    promptTokens = prompt,
                    completionTokens = completion,
                    cachedTokens = cached,
                    totalTokens = prompt + completion,
                )
            }

            emit(
                UIMessage(
                    role = MessageRole.ASSISTANT,
                    parts = parts,
                    usage = usage,
                    finished = true,
                ),
            )
        }
    }.flowOn(Dispatchers.IO)
}
