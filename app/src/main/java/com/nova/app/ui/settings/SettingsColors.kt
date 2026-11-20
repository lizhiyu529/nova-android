package com.nova.app.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** 设置页背景：主题 background。 */
@Composable
fun settingsPageBackground(): Color = MaterialTheme.colorScheme.background

/**
 * 设置卡片：与 RikkaHub 一致，用 secondaryContainer。
 * 它的对比度比 surfaceContainerLow 高得多，卡片边界清楚。
 */
@Composable
fun settingsCardColor(): Color = MaterialTheme.colorScheme.secondaryContainer

/** 对话框：用卡片色，浮在遮罩上。 */
@Composable
fun settingsDialogColor(): Color = MaterialTheme.colorScheme.secondaryContainer
