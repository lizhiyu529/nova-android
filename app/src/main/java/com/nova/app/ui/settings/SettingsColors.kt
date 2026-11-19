package com.nova.app.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
private fun isDark(): Boolean = MaterialTheme.colorScheme.surface.luminance() < 0.5f

/** 设置页背景：比卡片低一档的容器色。 */
@Composable
fun settingsPageBackground(): Color = MaterialTheme.colorScheme.background

/** 设置卡片：主题 surface，随配色方案变化。 */
@Composable
fun settingsCardColor(): Color = MaterialTheme.colorScheme.surface

/** 对话框：比卡片再亮一档，暗色下用高一阶容器色区分。 */
@Composable
fun settingsDialogColor(): Color =
    if (isDark()) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surface
