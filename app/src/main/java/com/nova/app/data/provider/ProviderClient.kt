package com.nova.app.data.provider

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

sealed interface FetchResult {
    data class Success(val models: List<String>) : FetchResult
    data class Failure(val message: String) : FetchResult
}

data class SpeedResult(
    val modelId: String,
    val latencyMs: Long,
    val reachable: Boolean,
    val message: String = "",
)

object ProviderClient {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun errorText(code: Int, body: String): String {
        val detail = runCatching {
            val json = JSONObject(body)
            val error = json.optJSONObject("error")
            error?.optString("message").orEmpty().ifBlank { json.optString("message") }
        }.getOrDefault("")
        val suffix = if (detail.isBlank()) "" else " · " + detail.take(120)
        return "HTTP $code$suffix"
    }

    private fun describe(throwable: Throwable): String = when (throwable) {
        is UnknownHostException -> "DNS 解析失败：找不到主机"
        is SocketTimeoutException -> "连接超时，检查地址和端口"
        is IOException -> throwable.message?.take(140) ?: "网络异常"
        else -> throwable.message?.take(140) ?: throwable.javaClass.simpleName
    }

    fun authHeaders(provider: Provider): Map<String, String> {
        if (provider.apiKey.isBlank()) return emptyMap()
        return mapOf("Authorization" to "Bearer ${provider.apiKey.trim()}")
    }

    suspend fun fetchModels(provider: Provider): FetchResult = withContext(Dispatchers.IO) {
        val endpoint = normalizeEndpoint(provider.endpoint, provider.allowHttp)
        if (endpoint.isEmpty()) {
            return@withContext FetchResult.Failure("端点为空，请先填写地址")
        }
        val url = modelsUrl(endpoint)
        runCatching {
            val request = Request.Builder()
                .url(url)
                .get()
                .apply { authHeaders(provider).forEach { (k, v) -> header(k, v) } }
                .build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext FetchResult.Failure(errorText(response.code, body))
                }
                val ids = parseModelIds(body)
                if (ids.isEmpty()) {
                    FetchResult.Failure("接口返回成功但没有解析到模型：$url")
                } else {
                    FetchResult.Success(ids)
                }
            }
        }.getOrElse { FetchResult.Failure(describe(it)) }
    }

    private fun parseModelIds(body: String): List<String> {
        return runCatching {
            val trimmed = body.trim()
            if (trimmed.startsWith("[")) {
                return parseArray(JSONArray(trimmed))
            }
            val root = JSONObject(trimmed)
            val data = root.optJSONArray("data")
                ?: root.optJSONArray("models")
                ?: root.optJSONArray("result")
            if (data != null) parseArray(data) else emptyList()
        }.getOrDefault(emptyList())
    }

    private fun parseArray(data: JSONArray): List<String> = buildList {
        for (index in 0 until data.length()) {
            val item = data.optJSONObject(index)
            val raw = when {
                item != null -> item.optString("id").ifBlank { item.optString("name") }
                else -> data.optString(index)
            }
            val id = raw.trim()
            if (id.isNotEmpty() && id != "null") add(id)
        }
    }.distinct().sorted()

    suspend fun testModel(provider: Provider, modelId: String): SpeedResult =
        withContext(Dispatchers.IO) {
            val endpoint = normalizeEndpoint(provider.endpoint, provider.allowHttp)
            if (endpoint.isEmpty()) {
                return@withContext SpeedResult(modelId, -1L, false, "端点为空")
            }
            val payload = JSONObject().apply {
                put("model", modelId)
                put("max_tokens", 1)
                put("stream", false)
                put(
                    "messages",
                    JSONArray().put(
                        JSONObject().apply {
                            put("role", "user")
                            put("content", "hi")
                        },
                    ),
                )
            }
            runCatching {
                val request = Request.Builder()
                    .url(chatUrl(endpoint))
                    .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))
                    .apply { authHeaders(provider).forEach { (k, v) -> header(k, v) } }
                    .build()

                val start = System.currentTimeMillis()
                client.newCall(request).execute().use { response ->
                    val elapsed = System.currentTimeMillis() - start
                    val body = response.body?.string().orEmpty()
                    when {
                        response.code == 401 || response.code == 403 ->
                            SpeedResult(modelId, elapsed, false, errorText(response.code, body))
                        response.isSuccessful ->
                            SpeedResult(modelId, elapsed, true)
                        response.code == 400 || response.code == 422 ->
                            SpeedResult(modelId, elapsed, true, errorText(response.code, body))
                        else ->
                            SpeedResult(modelId, elapsed, false, errorText(response.code, body))
                    }
                }
            }.getOrElse { SpeedResult(modelId, -1L, false, describe(it)) }
        }
}
