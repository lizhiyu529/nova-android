package com.nova.app.data

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object Locales {

    fun apply(tag: String) {
        AppCompatDelegate.setApplicationLocales(
            if (tag.isEmpty()) LocaleListCompat.getEmptyLocaleList()
            else LocaleListCompat.forLanguageTags(tag),
        )
    }

    fun wrap(context: Context): Context {
        val tag = AppSettings.languageTag(context)
        if (tag.isEmpty()) return context
        val locales = LocaleList.forLanguageTags(tag)
        val config = Configuration(context.resources.configuration)
        config.setLocales(locales)
        return context.createConfigurationContext(config)
    }
}
