package com.nova.app.data

import android.content.Context
import androidx.core.content.edit

object AppSettings {

    private const val PREFS = "nova.settings"
    private const val KEY_LANGUAGE = "language_tag"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun languageTag(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, "") ?: ""

    fun setLanguageTag(context: Context, tag: String) {
        prefs(context).edit { putString(KEY_LANGUAGE, tag) }
    }
}
