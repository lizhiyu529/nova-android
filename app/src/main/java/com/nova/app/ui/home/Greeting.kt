package com.nova.app.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.time.LocalDateTime

fun greetingFor(hour: Int): String = when (hour) {
    in 5..8 -> "早上好，今天想写点什么？"
    in 9..11 -> "上午好，有什么要搞的？"
    in 12..13 -> "中午好，先吃口饭再说"
    in 14..17 -> "下午好，接着干"
    in 18..22 -> "晚上好，今晚搞定它"
    else -> "夜深了，别熬太狠"
}

/** 分钟级刷新，避免每秒重组整棵界面。 */
@Composable
fun rememberCurrentTime(): LocalDateTime {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            val current = LocalDateTime.now()
            now = current
            delay(60_000L - current.second * 1_000L)
        }
    }
    return now
}
