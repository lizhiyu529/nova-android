package com.nova.app.ui.theme

import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun NovaTheme(
    darkTheme: Boolean,
    fontScale: Float = 1f,
    content: @Composable () -> Unit,
) {
    MaterialExpressiveTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        motionScheme = MotionScheme.expressive(),
        shapes = NovaShapes,
        typography = NovaTypography.scaled(fontScale),
        content = content,
    )
}

private fun TextUnit.scaleBy(factor: Float): TextUnit {
    if (!isSp) return this
    return (value * factor).sp
}

/** 按比例缩放整套字号；行高同步缩放，避免文字挤在一起。 */
fun Typography.scaled(factor: Float): Typography {
    if (factor == 1f) return this
    return copy(
        displayLarge = displayLarge.copy(fontSize = displayLarge.fontSize.scaleBy(factor), lineHeight = displayLarge.lineHeight.scaleBy(factor)),
        displayMedium = displayMedium.copy(fontSize = displayMedium.fontSize.scaleBy(factor), lineHeight = displayMedium.lineHeight.scaleBy(factor)),
        displaySmall = displaySmall.copy(fontSize = displaySmall.fontSize.scaleBy(factor), lineHeight = displaySmall.lineHeight.scaleBy(factor)),
        headlineLarge = headlineLarge.copy(fontSize = headlineLarge.fontSize.scaleBy(factor), lineHeight = headlineLarge.lineHeight.scaleBy(factor)),
        headlineMedium = headlineMedium.copy(fontSize = headlineMedium.fontSize.scaleBy(factor), lineHeight = headlineMedium.lineHeight.scaleBy(factor)),
        headlineSmall = headlineSmall.copy(fontSize = headlineSmall.fontSize.scaleBy(factor), lineHeight = headlineSmall.lineHeight.scaleBy(factor)),
        titleLarge = titleLarge.copy(fontSize = titleLarge.fontSize.scaleBy(factor), lineHeight = titleLarge.lineHeight.scaleBy(factor)),
        titleMedium = titleMedium.copy(fontSize = titleMedium.fontSize.scaleBy(factor), lineHeight = titleMedium.lineHeight.scaleBy(factor)),
        titleSmall = titleSmall.copy(fontSize = titleSmall.fontSize.scaleBy(factor), lineHeight = titleSmall.lineHeight.scaleBy(factor)),
        bodyLarge = bodyLarge.copy(fontSize = bodyLarge.fontSize.scaleBy(factor), lineHeight = bodyLarge.lineHeight.scaleBy(factor)),
        bodyMedium = bodyMedium.copy(fontSize = bodyMedium.fontSize.scaleBy(factor), lineHeight = bodyMedium.lineHeight.scaleBy(factor)),
        bodySmall = bodySmall.copy(fontSize = bodySmall.fontSize.scaleBy(factor), lineHeight = bodySmall.lineHeight.scaleBy(factor)),
        labelLarge = labelLarge.copy(fontSize = labelLarge.fontSize.scaleBy(factor), lineHeight = labelLarge.lineHeight.scaleBy(factor)),
        labelMedium = labelMedium.copy(fontSize = labelMedium.fontSize.scaleBy(factor), lineHeight = labelMedium.lineHeight.scaleBy(factor)),
        labelSmall = labelSmall.copy(fontSize = labelSmall.fontSize.scaleBy(factor), lineHeight = labelSmall.lineHeight.scaleBy(factor)),
        headlineLargeEmphasized = headlineLargeEmphasized.copy(
            fontSize = headlineLargeEmphasized.fontSize.scaleBy(factor),
            lineHeight = headlineLargeEmphasized.lineHeight.scaleBy(factor),
        ),
        headlineMediumEmphasized = headlineMediumEmphasized.copy(
            fontSize = headlineMediumEmphasized.fontSize.scaleBy(factor),
            lineHeight = headlineMediumEmphasized.lineHeight.scaleBy(factor),
        ),
    )
}
