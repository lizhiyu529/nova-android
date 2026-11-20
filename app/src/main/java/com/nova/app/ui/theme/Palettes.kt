package com.nova.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * 配色体系，结构参考 RikkaHub 的 preset 主题。
 *
 * 关键约定：
 * - surface 与 background 同色，卡片层次靠 surfaceContainer* 系列区分。
 *   因此页面背景用 background，卡片必须用 surfaceContainerLow，
 *   不能用 surface，否则卡片和背景会糊在一起。
 * - 灰阶是全局唯一的一套，配色方案只替换强调色（primary/secondary/tertiary）。
 * - 浅色和暗色是两套独立设计，各自调整对比度。
 */
object Palette {

    // ---------- 浅色 ----------
    private val lightBackground = Color(0xFFEDEDF0)
    private val lightSurface = Color(0xFFEDEDF0)
    private val lightOnSurface = Color(0xFF16181D)
    private val lightOnSurfaceVariant = Color(0xFF5E6269)
    private val lightSurfaceVariant = Color(0xFFE4E5E9)
    private val lightOutline = Color(0xFFBCC0C6)
    private val lightOutlineVariant = Color(0xFFD5D6DB)
    private val lightScrim = Color(0xFF000000)
    private val lightInverseSurface = Color(0xFF2A2C31)
    private val lightInverseOnSurface = Color(0xFFF3F4F6)
    private val lightSurfaceDim = Color(0xFFE5E5E9)
    private val lightSurfaceBright = Color(0xFFFFFFFF)
    private val lightContainerLowest = Color(0xFFFFFFFF)
    private val lightContainerLow = Color(0xFFFFFFFF)
    private val lightContainer = Color(0xFFF7F7F9)
    private val lightContainerHigh = Color(0xFFE4E5E9)
    private val lightContainerHighest = Color(0xFFD5D6DB)

    // ---------- 暗色 ----------
    private val darkBackground = Color(0xFF0F1012)
    private val darkSurface = Color(0xFF0F1012)
    private val darkOnSurface = Color(0xFFE6E7EA)
    private val darkOnSurfaceVariant = Color(0xFFA0A4AB)
    private val darkSurfaceVariant = Color(0xFF2B2D33)
    private val darkOutline = Color(0xFF5A5D64)
    private val darkOutlineVariant = Color(0xFF3A3C42)
    private val darkScrim = Color(0xFF000000)
    private val darkInverseSurface = Color(0xFFE6E7EA)
    private val darkInverseOnSurface = Color(0xFF2A2C31)
    private val darkSurfaceDim = Color(0xFF0F1012)
    private val darkSurfaceBright = Color(0xFF35373C)
    private val darkContainerLowest = Color(0xFF0C0D0F)
    private val darkContainerLow = Color(0xFF232429)
    private val darkContainer = Color(0xFF1B1C21)
    private val darkContainerHigh = Color(0xFF2B2D33)
    private val darkContainerHighest = Color(0xFF35373D)

    fun light(palette: PaletteScheme): ColorScheme {
        val a = palette.lightAccent()
        return lightColorScheme(
            primary = a.primary,
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = a.primaryContainer,
            onPrimaryContainer = a.onPrimaryContainer,
            secondary = a.secondary,
            onSecondary = a.onSecondary,
            secondaryContainer = a.secondaryContainer,
            onSecondaryContainer = a.onSecondaryContainer,
            tertiary = a.tertiary,
            onTertiary = a.onTertiary,
            tertiaryContainer = a.tertiaryContainer,
            onTertiaryContainer = a.onTertiaryContainer,
            error = Color(0xFFD93A32),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFDE7E5),
            onErrorContainer = Color(0xFFA31710),
            background = lightBackground,
            onBackground = lightOnSurface,
            surface = lightSurface,
            onSurface = lightOnSurface,
            surfaceVariant = lightSurfaceVariant,
            onSurfaceVariant = lightOnSurfaceVariant,
            outline = lightOutline,
            outlineVariant = lightOutlineVariant,
            scrim = lightScrim,
            inverseSurface = lightInverseSurface,
            inverseOnSurface = lightInverseOnSurface,
            inversePrimary = a.primary,
            surfaceTint = a.primary,
            surfaceDim = lightSurfaceDim,
            surfaceBright = lightSurfaceBright,
            surfaceContainerLowest = lightContainerLowest,
            surfaceContainerLow = lightContainerLow,
            surfaceContainer = lightContainer,
            surfaceContainerHigh = lightContainerHigh,
            surfaceContainerHighest = lightContainerHighest,
        )
    }

    fun dark(palette: PaletteScheme): ColorScheme {
        val a = palette.darkAccent()
        return darkColorScheme(
            primary = a.primary,
            onPrimary = a.onPrimary,
            primaryContainer = a.primaryContainer,
            onPrimaryContainer = a.onPrimaryContainer,
            secondary = a.secondary,
            onSecondary = a.onSecondary,
            secondaryContainer = a.secondaryContainer,
            onSecondaryContainer = a.onSecondaryContainer,
            tertiary = a.tertiary,
            onTertiary = a.onTertiary,
            tertiaryContainer = a.tertiaryContainer,
            onTertiaryContainer = a.onTertiaryContainer,
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = darkBackground,
            onBackground = darkOnSurface,
            surface = darkSurface,
            onSurface = darkOnSurface,
            surfaceVariant = darkSurfaceVariant,
            onSurfaceVariant = darkOnSurfaceVariant,
            outline = darkOutline,
            outlineVariant = darkOutlineVariant,
            scrim = darkScrim,
            inverseSurface = darkInverseSurface,
            inverseOnSurface = darkInverseOnSurface,
            inversePrimary = palette.lightAccent().primary,
            surfaceTint = a.primary,
            surfaceDim = darkSurfaceDim,
            surfaceBright = darkSurfaceBright,
            surfaceContainerLowest = darkContainerLowest,
            surfaceContainerLow = darkContainerLow,
            surfaceContainer = darkContainer,
            surfaceContainerHigh = darkContainerHigh,
            surfaceContainerHighest = darkContainerHighest,
        )
    }

    /** 浮层（Toast、菜单）：比最高阶容器再亮一档。 */
    fun overlay(dark: Boolean): Color {
        return if (dark) Color(0xFF3E4046) else Color(0xFF2A2C31)
    }

    fun onOverlay(): Color {
        return Color(0xFFF3F4F6)
    }

    /** 卡片色：页面背景之上的一层，必须与 background 区分。 */
    fun card(dark: Boolean): Color {
        return if (dark) darkContainerLow else lightContainerLow
    }
}

/** 配色预览用的三色小样。 */
data class PalettePreview(
    val background: Color,
    val card: Color,
    val accent: Color,
)

fun PaletteScheme.preview(): PalettePreview {
    return PalettePreview(
        background = Color(0xFFF7F7F9),
        card = Color(0xFFFFFFFF),
        accent = lightAccent().primary,
    )
}
