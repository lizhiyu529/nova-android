package com.nova.app.data

import android.content.Context
import androidx.core.content.edit

object AppSettings {

    private const val PREFS = "nova.settings"
    private const val KEY_LANGUAGE = "language_tag"
    private const val KEY_THEME = "theme_mode"
    private const val KEY_FONT_INDEX = "font_scale_index"
    private const val KEY_FOLLOW_SYSTEM_FONT = "follow_system_font"
    private const val KEY_AUTO_COLLAPSE = "auto_collapse_thinking"

    const val DEFAULT_THEME = "system"
    const val DEFAULT_FONT_INDEX = 3

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun languageTag(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, "") ?: ""

    fun setLanguageTag(context: Context, tag: String) {
        prefs(context).edit { putString(KEY_LANGUAGE, tag) }
    }

    fun themeMode(context: Context): String =
        prefs(context).getString(KEY_THEME, DEFAULT_THEME) ?: DEFAULT_THEME

    fun setThemeMode(context: Context, value: String) {
        prefs(context).edit { putString(KEY_THEME, value) }
    }

    fun fontScaleIndex(context: Context): Int =
        prefs(context).getInt(KEY_FONT_INDEX, DEFAULT_FONT_INDEX)

    fun setFontScaleIndex(context: Context, value: Int) {
        prefs(context).edit { putInt(KEY_FONT_INDEX, value) }
    }

    fun followSystemFont(context: Context): Boolean =
        prefs(context).getBoolean(KEY_FOLLOW_SYSTEM_FONT, false)

    fun setFollowSystemFont(context: Context, value: Boolean) {
        prefs(context).edit { putBoolean(KEY_FOLLOW_SYSTEM_FONT, value) }
    }

    fun autoCollapseThinking(context: Context): Boolean =
        prefs(context).getBoolean(KEY_AUTO_COLLAPSE, true)

    fun setAutoCollapseThinking(context: Context, value: Boolean) {
        prefs(context).edit { putBoolean(KEY_AUTO_COLLAPSE, value) }
    }
}
