package com.nova.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

internal fun NovaColors.toColorScheme(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = accent,
        onPrimary = onAccent,
        primaryContainer = accentSubtle,
        onPrimaryContainer = onAccentSubtle,
        inversePrimary = inverseAccent,
        secondary = textSecondary,
        onSecondary = card,
        secondaryContainer = containerHigh,
        onSecondaryContainer = textPrimary,
        tertiary = textTertiary,
        onTertiary = card,
        tertiaryContainer = containerHigh,
        onTertiaryContainer = textPrimary,
        background = pageBackground,
        onBackground = textPrimary,
        surface = card,
        onSurface = textPrimary,
        surfaceVariant = containerHigh,
        onSurfaceVariant = textSecondary,
        surfaceTint = surfaceTint,
        surfaceBright = surfaceBright,
        surfaceDim = surfaceDim,
        surfaceContainerLowest = containerLowest,
        surfaceContainerLow = containerLow,
        surfaceContainer = container,
        surfaceContainerHigh = containerHigh,
        surfaceContainerHighest = containerHighest,
        error = danger,
        onError = onDanger,
        errorContainer = containerHigh,
        onErrorContainer = textPrimary,
        outline = border,
        outlineVariant = divider,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        scrim = scrim,
    )
}
