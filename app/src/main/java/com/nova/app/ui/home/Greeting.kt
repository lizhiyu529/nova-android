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

/**
 * 只在问候语真正需要变化时才更新状态：跨过时段边界或跨天，
 * 避免每分钟把整棵界面树重组一次。
 */
@Composable
fun rememberGreeting(): String {
    var greeting by remember { mutableStateOf(greetingFor(LocalDateTime.now().hour)) }
    LaunchedEffect(Unit) {
        while (true) {
            val current = LocalDateTime.now()
            val next = greetingFor(current.hour)
            if (next != greeting) greeting = next
            delay(60_000L - current.second * 1_000L)
        }
    }
    return greeting
}
