package com.nova.app.data.provider

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
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
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun requestBuilder(url: String, provider: Provider): Request.Builder {
        val builder = Request.Builder().url(url).get()
        if (provider.apiKey.isNotBlank()) {
            builder.header("Authorization", "Bearer ${provider.apiKey.trim()}")
        }
        return builder
    }

    suspend fun fetchModels(provider: Provider): FetchResult = withContext(Dispatchers.IO) {
        val endpoint = normalizeEndpoint(provider.endpoint, provider.allowHttp)
        if (endpoint.isEmpty()) return@withContext FetchResult.Failure("empty endpoint")
        runCatching {
            val request = requestBuilder(modelsUrl(endpoint), provider).build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext FetchResult.Failure("HTTP ${response.code}")
                }
                val ids = parseModelIds(body)
                if (ids.isEmpty()) FetchResult.Failure("empty list")
                else FetchResult.Success(ids)
            }
        }.getOrElse { FetchResult.Failure(it.message ?: "network error") }
    }

    private fun parseModelIds(body: String): List<String> {
        return runCatching {
            val root = JSONObject(body)
            val data = root.optJSONArray("data") ?: root.optJSONArray("models") ?: return emptyList()
            buildList {
                for (index in 0 until data.length()) {
                    val item = data.optJSONObject(index)
                    val raw = item?.optString("id") ?: data.optString(index)
                    val id = raw?.trim().orEmpty()
                    if (id.isNotEmpty() && id != "null") add(id)
                }
            }.distinct().sorted()
        }.getOrDefault(emptyList())
    }

    suspend fun testModel(provider: Provider, modelId: String): SpeedResult =
        withContext(Dispatchers.IO) {
            val endpoint = normalizeEndpoint(provider.endpoint, provider.allowHttp)
            if (endpoint.isEmpty()) {
                return@withContext SpeedResult(modelId, -1L, false, "empty endpoint")
            }
            runCatching {
                val payload = JSONObject().apply {
                    put("model", modelId)
                    put("max_tokens", 1)
                    put("messages", org.json.JSONArray().put(
                        JSONObject().apply {
                            put("role", "user")
                            put("content", "hi")
                        },
                    ))
                }
                val request = Request.Builder()
                    .url(chatUrl(endpoint))
                    .post(
                        okhttp3.RequestBody.create(
                            okhttp3.MediaType.parse("application/json; charset=utf-8"),
                            payload.toString(),
                        ),
                    )
                    .also { builder ->
                        if (provider.apiKey.isNotBlank()) {
                            builder.header("Authorization", "Bearer ${provider.apiKey.trim()}")
                        }
                    }
                    .build()

                val start = System.currentTimeMillis()
                client.newCall(request).execute().use { response ->
                    val elapsed = System.currentTimeMillis() - start
                    val body = response.body?.string().orEmpty()
                    val ok = response.isSuccessful || response.code == 400 ||
                        response.code == 422 || body.contains("model")
                    if (response.code == 401 || response.code == 403) {
                        SpeedResult(modelId, elapsed, false, "auth")
                    } else if (ok) {
                        SpeedResult(modelId, elapsed, true)
                    } else {
                        SpeedResult(modelId, elapsed, false, "HTTP ${response.code}")
                    }
                }
            }.getOrElse { SpeedResult(modelId, -1L, false, it.message ?: "network error") }
        }
}
