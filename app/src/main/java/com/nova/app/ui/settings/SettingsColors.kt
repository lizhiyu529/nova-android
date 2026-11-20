package com.nova.app.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** 设置页背景：主题 background，比卡片低一档。 */
@Composable
fun settingsPageBackground(): Color = MaterialTheme.colorScheme.background

/** 设置卡片：surface 与 background 同色，卡片必须用容器色区分。 */
@Composable
fun settingsCardColor(): Color = MaterialTheme.colorScheme.surfaceContainerLow

/** 对话框：再高一层，浮在遮罩上。 */
@Composable
fun settingsDialogColor(): Color = MaterialTheme.colorScheme.surfaceContainerLow
