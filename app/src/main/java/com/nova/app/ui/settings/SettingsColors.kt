package com.nova.app.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** 设置页背景：iOS 分组列表用灰底，白卡片浮在上面。 */
@Composable
fun settingsPageBackground(): Color = MaterialTheme.colorScheme.surfaceContainerLow

/** 设置卡片：纯白（暗色下是 systemGray6）。 */
@Composable
fun settingsCardColor(): Color = MaterialTheme.colorScheme.surface

/** 对话框：同卡片色。 */
@Composable
fun settingsDialogColor(): Color = MaterialTheme.colorScheme.surface
