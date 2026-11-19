package com.nova.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val BrandBlue = Color(0xFF3D5AFE)
private val BrandBlueLight = Color(0xFF8C9EFF)
private val Ink = Color(0xFF111318)
private val Paper = Color(0xFFFCFCFF)

val LightColors = lightColorScheme(
    primary = Color(0xFF3B55D9),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDEE1FF),
    onPrimaryContainer = Color(0xFF00105C),
    secondary = Color(0xFF5A5D72),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDFE1F9),
    onSecondaryContainer = Color(0xFF171A2C),
    tertiary = Color(0xFF76546E),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD7F0),
    onTertiaryContainer = Color(0xFF2C1229),
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE2E1EC),
    onSurfaceVariant = Color(0xFF45464F),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF7F7FC),
    surfaceContainer = Color(0xFFF1F1F7),
    surfaceContainerHigh = Color(0xFFEBEBF2),
    surfaceContainerHighest = Color(0xFFE5E5EC),
    outline = Color(0xFF767680),
    outlineVariant = Color(0xFFC7C6D0),
    inverseSurface = Color(0xFF2F3036),
    inverseOnSurface = Color(0xFFF1F0F7),
    inversePrimary = BrandBlueLight,
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFB8C3FF),
    onPrimary = Color(0xFF08218A),
    primaryContainer = Color(0xFF253EA4),
    onPrimaryContainer = Color(0xFFDEE1FF),
    secondary = Color(0xFFC3C5DD),
    onSecondary = Color(0xFF2C2F42),
    secondaryContainer = Color(0xFF424659),
    onSecondaryContainer = Color(0xFFDFE1F9),
    tertiary = Color(0xFFE5BAD8),
    onTertiary = Color(0xFF44273F),
    tertiaryContainer = Color(0xFF5D3D56),
    onTertiaryContainer = Color(0xFFFFD7F0),
    background = Color(0xFF121214),
    onBackground = Color(0xFFF2F2F5),
    surface = Color(0xFF121214),
    onSurface = Color(0xFFF2F2F5),
    surfaceVariant = Color(0xFF3A3A3E),
    onSurfaceVariant = Color(0xFFA8A8B0),
    surfaceContainerLowest = Color(0xFF161618),
    surfaceContainerLow = Color(0xFF1E1E21),
    surfaceContainer = Color(0xFF26262A),
    surfaceContainerHigh = Color(0xFF2E2E32),
    surfaceContainerHighest = Color(0xFF3A3A3E),
    outline = Color(0xFF8A8A92),
    outlineVariant = Color(0xFF3A3A3E),
    inverseSurface = Color(0xFFF2F2F5),
    inverseOnSurface = Color(0xFF1B1B1E),
    inversePrimary = Color(0xFF3B55D9),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF3B0907),
    errorContainer = Color(0xFF5C1A17),
    onErrorContainer = Color(0xFFFFDAD6),
)
