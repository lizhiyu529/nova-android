package com.nova.app.ui.settings

import androidx.annotation.StringRes
import com.nova.app.R

enum class AppLanguage(@StringRes val labelRes: Int, val tag: String) {
    System(R.string.language_system, ""),
    Chinese(R.string.language_chinese, "zh"),
    English(R.string.language_english, "en"),
    ;

    companion object {
        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { it.tag == tag } ?: System
    }
}

enum class AppThemeMode(@StringRes val labelRes: Int, val key: String) {
    System(R.string.theme_system, "system"),
    Light(R.string.theme_light, "light"),
    Dark(R.string.theme_dark, "dark"),
    ;

    companion object {
        fun fromKey(key: String?): AppThemeMode =
            entries.firstOrNull { it.key == key } ?: System
    }
}
