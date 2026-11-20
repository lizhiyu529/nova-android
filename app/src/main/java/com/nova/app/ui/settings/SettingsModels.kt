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

enum class AppThemeMode(@StringRes val labelRes: Int) {
    System(R.string.theme_system),
    Light(R.string.theme_light),
    Dark(R.string.theme_dark),
}
