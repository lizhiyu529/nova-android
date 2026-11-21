package com.nova.app.data.chat

import com.nova.app.data.provider.chatUrl
import com.nova.app.data.provider.normalizeEndpoint
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
import org.json.JSONArray
import org.json.JSONObject

object ChatClient {

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

    private fun buildBody(request: ChatRequest): String {
        val messages = JSONArray()
        if (request.systemPrompt.isNotBlank()) {
            messages.put(
                JSONObject().apply {
                    put("role", "system")
                    put("content", request.systemPrompt)
                },
            )
        }
        request.messages.forEach { turn ->
            messages.put(
                JSONObject().apply {
                    put("role", turn.role)
                    put("content", turn.content)
                    if (turn.name.isNotBlank() && turn.role != "tool") put("name", turn.name)
                    if (turn.toolCallId.isNotBlank()) put("tool_call_id", turn.toolCallId)
                },
            )
        }

        return JSONObject().apply {
            put("model", request.model)
            put("messages", messages)
            put("stream", request.stream)
            request.temperature?.let { put("temperature", it) }
            request.maxTokens?.let { put("max_tokens", it) }
            request.reasoning?.let { put("reasoning_effort", it) }
            if (request.stream) {
                put("stream_options", JSONObject().put("include_usage", true))
            }
            if (request.tools.isNotEmpty()) {
                val tools = JSONArray()
                request.tools.forEach { tool ->
                    tools.put(
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
                put("tools", tools)
            }
        }.toString()
    }

    private fun buildRequest(request: ChatRequest, stream: Boolean): Request {
        val endpoint = normalizeEndpoint(request.provider.endpoint, request.provider.allowHttp)
        val body = buildBody(request.copy(stream = stream))
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
            error?.optString("message").orEmpty().ifBlank { json.optString("message") }
        }.getOrDefault("")
        return ChatError.Http(code, detail.ifBlank { body.take(200) })
    }

    fun send(request: ChatRequest): Flow<ChatChunk> = if (request.stream) {
        streamFlow(request)
    } else {
        nonStreamFlow(request)
    }

    private fun nonStreamFlow(request: ChatRequest): Flow<ChatChunk> = flow {
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
            root.optJSONObject("usage")?.let { usage ->
                val prompt = usage.optInt("prompt_tokens", 0)
                val completion = usage.optInt("completion_tokens", 0)
                if (prompt > 0 || completion > 0) emit(ChatChunk.Usage(prompt, completion))
            }
            val choices = root.optJSONArray("choices")
                ?: throw ChatException(ChatError.InvalidResponse("缺少 choices"))
            val first = choices.optJSONObject(0)
                ?: throw ChatException(ChatError.InvalidResponse("choices 为空"))
            val message = first.optJSONObject("message")
            val content = message?.optString("content").orEmpty()
            if (content.isEmpty()) {
                throw ChatException(ChatError.InvalidResponse("返回内容为空"))
            }
            emit(ChatChunk.Delta(content))
            emit(ChatChunk.Done)
        }
    }.flowOn(Dispatchers.IO)

    private fun streamFlow(request: ChatRequest): Flow<ChatChunk> = callbackFlow {
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
                    try {
                        val reader = source.charStream().buffered()
                        OpenAiStream.read(reader) { chunk ->
                            trySend(chunk)
                        }
                        close()
                    } catch (e: Exception) {
                        close(ChatException(describe(e)))
                    }
                }
            }
        })

        awaitClose { call.cancel() }
    }.flowOn(Dispatchers.IO)
}
