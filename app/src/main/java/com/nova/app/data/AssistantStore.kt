package com.nova.app.data

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray

data class AssistantProfile(
    val prompt: String = "",
    val promptEnabled: Boolean = true,
    val memories: List<String> = emptyList(),
    val memoryEnabled: Boolean = true,
)

object AssistantStore {

    private const val PREFS = "nova.assistant"
    private const val KEY_PROMPT = "prompt"
    private const val KEY_PROMPT_ENABLED = "prompt_enabled"
    private const val KEY_MEMORIES = "memories"
    private const val KEY_MEMORY_ENABLED = "memory_enabled"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(context: Context): AssistantProfile {
        val p = prefs(context)
        return AssistantProfile(
            prompt = p.getString(KEY_PROMPT, "") ?: "",
            promptEnabled = p.getBoolean(KEY_PROMPT_ENABLED, true),
            memories = decode(p.getString(KEY_MEMORIES, null)),
            memoryEnabled = p.getBoolean(KEY_MEMORY_ENABLED, true),
        )
    }

    fun save(context: Context, profile: AssistantProfile) {
        prefs(context).edit {
            putString(KEY_PROMPT, profile.prompt)
            putBoolean(KEY_PROMPT_ENABLED, profile.promptEnabled)
            putString(KEY_MEMORIES, encode(profile.memories))
            putBoolean(KEY_MEMORY_ENABLED, profile.memoryEnabled)
        }
    }

    fun buildSystemPrompt(profile: AssistantProfile): String {
        val blocks = buildList {
            if (profile.promptEnabled && profile.prompt.isNotBlank()) {
                add(profile.prompt.trim())
            }
            if (profile.memoryEnabled) {
                val items = profile.memories.map { it.trim() }.filter { it.isNotEmpty() }
                if (items.isNotEmpty()) {
                    add(items.joinToString(separator = "\n") { "- $it" })
                }
            }
        }
        return blocks.joinToString(separator = "\n\n")
    }

    private fun encode(memories: List<String>): String {
        val array = JSONArray()
        memories.forEach { array.put(it) }
        return array.toString()
    }

    private fun decode(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.optString(index)
                    if (item.isNotBlank()) add(item)
                }
            }
        }.getOrDefault(emptyList())
    }
}
