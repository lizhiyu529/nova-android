package com.nova.app.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.LocalDateTime
import kotlinx.coroutines.delay

data class Greeting(val title: String, val hint: String)

private fun greetingFor(hour: Int): Greeting = when (hour) {
    in 5..8 -> Greeting(
        title = "早上好",
        hint = "脑子最清醒的时候，先啃硬骨头",
    )
    in 9..11 -> Greeting(
        title = "上午好",
        hint = "今天想先解决什么问题？",
    )
    in 12..13 -> Greeting(
        title = "中午好",
        hint = "吃口饭再回来，代码不会跑",
    )
    in 14..17 -> Greeting(
        title = "下午好",
        hint = "有点困的话，来杯咖啡继续",
    )
    in 18..22 -> Greeting(
        title = "晚上好",
        hint = "安静的时候最适合写代码",
    )
    else -> Greeting(
        title = "夜深了",
        hint = "早点睡，明天再修也不迟",
    )
}

@Composable
fun rememberGreeting(): Greeting {
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
