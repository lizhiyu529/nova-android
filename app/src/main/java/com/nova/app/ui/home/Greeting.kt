package com.nova.app.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.Strings
import java.time.LocalDateTime
import kotlinx.coroutines.delay

data class Greeting(val title: String, val hint: String)

private fun greetingFor(hour: Int, strings: Strings): Greeting = when (hour) {
    in 5..8 -> Greeting(strings.greetingMorningTitle, strings.greetingMorningHint)
    in 9..11 -> Greeting(strings.greetingForenoonTitle, strings.greetingForenoonHint)
    in 12..13 -> Greeting(strings.greetingNoonTitle, strings.greetingNoonHint)
    in 14..17 -> Greeting(strings.greetingAfternoonTitle, strings.greetingAfternoonHint)
    in 18..22 -> Greeting(strings.greetingEveningTitle, strings.greetingEveningHint)
    else -> Greeting(strings.greetingNightTitle, strings.greetingNightHint)
}

@Composable
fun rememberGreeting(): Greeting {
    val strings = NovaStrings.strings
    var greeting by remember { mutableStateOf(greetingFor(LocalDateTime.now().hour, strings)) }
    LaunchedEffect(Unit) {
        while (true) {
            val current = LocalDateTime.now()
            val next = greetingFor(current.hour, strings)
            if (next != greeting) greeting = next
            delay(60_000L - current.second * 1_000L)
        }
    }
    return greeting
}
