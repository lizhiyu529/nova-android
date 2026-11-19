package com.nova.app.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
private fun isDark(): Boolean = MaterialTheme.colorScheme.surface.luminance() < 0.5f

/** 设置页需要「背景比卡片暗」的层次，Material 的容器色阶在暗色下顺序相反，单独取。 */
@Composable
fun settingsPageBackground(): Color =
    if (isDark()) Color(0xFF121214) else Color(0xFFF2F2F5)

@Composable
fun settingsCardColor(): Color =
    if (isDark()) Color(0xFF1E1E21) else Color(0xFFFFFFFF)

@Composable
fun settingsDialogColor(): Color =
    if (isDark()) Color(0xFF2B2B2E) else Color(0xFFFFFFFF)
