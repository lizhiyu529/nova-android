package com.nova.app.ui.settings

enum class AppLanguage(val label: String, val tag: String) {
    System("系统", "system"),
    Chinese("中文 (简体中文)", "zh-CN"),
    Japanese("日本語", "ja-JP"),
    Arabic("العربية", "ar"),
    Armenian("Հայերեն", "hy-AM"),
    Azerbaijani("azərbaycan", "az-AZ"),
}

enum class AppThemeMode(val label: String) {
    System("系统"),
    Light("浅色"),
    Dark("深色"),
}
