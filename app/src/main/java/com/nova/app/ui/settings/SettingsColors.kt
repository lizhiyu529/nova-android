package com.nova.app.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** 设置页需要「背景比卡片暗」的层次，Material 的容器色阶在暗色下顺序相反，单独取。 */
@Composable
fun settingsPageBackground(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (scheme.surface.luminance() < 0.5f) {
        Color(0xFF0D0D0F)
    } else {
        Color(0xFFF2F2F5)
    }
}

@Composable
fun settingsCardColor(): Color {
    val scheme = MaterialTheme.colorScheme
    return if (scheme.surface.luminance() < 0.5f) {
        Color(0xFF212124)
    } else {
        Color(0xFFFFFFFF)
    }
}
