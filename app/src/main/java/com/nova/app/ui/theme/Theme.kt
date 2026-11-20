package com.nova.app.ui.theme

import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

val LocalNovaColors = staticCompositionLocalOf { NovaColorsLight }

object NovaTheme {

    val colors: NovaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNovaColors.current

    @Composable
    operator fun invoke(
        darkTheme: Boolean,
        fontScale: Float = 1f,
        content: @Composable () -> Unit,
    ) {
        val density = LocalDensity.current
        val scaledDensity = if (fontScale == 1f) density else Density(density.density, fontScale)
        val colors = if (darkTheme) NovaColorsDark else NovaColorsLight
        CompositionLocalProvider(
            LocalNovaColors provides colors,
            LocalDensity provides scaledDensity,
        ) {
            MaterialExpressiveTheme(
                colorScheme = colors.toColorScheme(),
                motionScheme = MotionScheme.expressive(),
                shapes = NovaShapes,
                typography = NovaTypography,
                content = content,
            )
        }
    }
}
