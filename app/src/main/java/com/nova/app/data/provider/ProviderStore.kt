package com.nova.app.data.provider

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject

object ProviderStore {

    private const val PREFS = "nova.providers"
    private const val KEY_PROVIDERS = "list"
    private const val KEY_ACTIVE = "active_id"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(context: Context): List<Provider> {
        val raw = prefs(context).getString(KEY_PROVIDERS, null)
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optJSONObject(index) ?: continue
                    add(item.toProvider())
                }
            }
        }.getOrDefault(emptyList())
    }

    fun save(context: Context, providers: List<Provider>) {
        val array = JSONArray()
        providers.forEach { array.put(it.toJson()) }
        prefs(context).edit { putString(KEY_PROVIDERS, array.toString()) }
    }

    fun activeId(context: Context): String =
        prefs(context).getString(KEY_ACTIVE, "") ?: ""

    fun setActiveId(context: Context, id: String) {
        prefs(context).edit { putString(KEY_ACTIVE, id) }
    }

    fun upsert(context: Context, provider: Provider): List<Provider> {
        val current = load(context)
        val next = if (current.any { it.id == provider.id }) {
            current.map { if (it.id == provider.id) provider else it }
        } else {
            current + provider
        }
        save(context, next)
        if (activeId(context).isEmpty()) setActiveId(context, provider.id)
        return next
    }

    fun delete(context: Context, id: String): List<Provider> {
        val next = load(context).filterNot { it.id == id }
        save(context, next)
        if (activeId(context) == id) setActiveId(context, next.firstOrNull()?.id.orEmpty())
        return next
    }

    private fun Provider.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("name", name)
        put("apiKey", apiKey)
        put("endpoint", endpoint)
        put("allowHttp", allowHttp)
        put("createdAt", createdAt)
        put("models", JSONArray().apply {
            models.forEach { model ->
                put(JSONObject().apply {
                    put("id", model.id)
                    put("latencyMs", model.latencyMs)
                    put("reachable", model.reachable)
                })
            }
        })
    }

    private fun JSONObject.toProvider(): Provider {
        val modelsArray = optJSONArray("models") ?: JSONArray()
        val models = buildList {
            for (index in 0 until modelsArray.length()) {
                val item = modelsArray.optJSONObject(index) ?: continue
                val id = item.optString("id")
                if (id.isNotBlank()) {
                    add(
                        ProviderModel(
                            id = id,
                            latencyMs = item.optLong("latencyMs", -1L),
                            reachable = item.optBoolean("reachable", false),
                        ),
                    )
                }
            }
        }
        return Provider(
            id = optString("id"),
            name = optString("name"),
            apiKey = optString("apiKey"),
            endpoint = optString("endpoint"),
            allowHttp = optBoolean("allowHttp", false),
            models = models,
            createdAt = optLong("createdAt", 0L),
        )
    }
}
