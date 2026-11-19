package com.nova.app.ui.home

import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val TimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun greetingFor(hour: Int): String = when (hour) {
    in 5..8 -> "早上好"
    in 9..11 -> "上午好"
    in 12..13 -> "中午好"
    in 14..17 -> "下午好"
    in 18..22 -> "晚上好"
    else -> "夜深了"
}

fun formatClock(time: LocalTime): String = time.format(TimeFormatter)
