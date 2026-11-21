package com.nova.app.data.provider

import android.content.Context
import androidx.core.content.edit

object GenerationStore {

    private const val PREFS = "nova.generation"
    private const val KEY_REASONING = "reasoning"
    private const val KEY_STREAM = "stream"
    private const val KEY_TOOLS = "tools"
    private const val KEY_ROUNDS = "max_rounds"
    private const val KEY_AUTO_TITLE = "auto_title"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(context: Context): GenerationSettings {
        val p = prefs(context)
        return GenerationSettings(
            reasoning = ReasoningEffort.fromKey(p.getString(KEY_REASONING, null)),
            stream = p.getBoolean(KEY_STREAM, true),
            toolsEnabled = p.getBoolean(KEY_TOOLS, true),
            maxToolRounds = p.getInt(KEY_ROUNDS, 3),
            autoTitle = p.getBoolean(KEY_AUTO_TITLE, true),
        )
    }

    fun save(context: Context, settings: GenerationSettings) {
        prefs(context).edit {
            putString(KEY_REASONING, settings.reasoning.key)
            putBoolean(KEY_STREAM, settings.stream)
            putBoolean(KEY_TOOLS, settings.toolsEnabled)
            putInt(KEY_ROUNDS, settings.maxToolRounds)
            putBoolean(KEY_AUTO_TITLE, settings.autoTitle)
        }
    }
}
