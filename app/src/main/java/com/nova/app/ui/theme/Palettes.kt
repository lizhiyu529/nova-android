package com.nova.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * 自制配色体系。
 *
 * 设计原则：
 * 1. 灰阶统一走 7 级中性阶梯（L0 最亮 → L6 最暗），不掺任何色相，
 *    任何组件需要「低一档 / 高一档」都从这套阶梯取，保证全局层次一致。
 * 2. 强调色只用于可交互元素（主按钮、选中态、输入光标），
 *    背景与卡片永不使用强调色，避免花哨。
 * 3. 文字对比度硬约束：主文字 ≥ 12:1，次要文字 ≥ 7:1，禁用态 ≥ 4:1。
 * 4. 浅色与暗色是两套独立设计，不是简单反转。
 *
 * 浅色阶梯（背景→卡片→更亮）
 *   L0 #FFFFFF  纯白       卡片、对话框
 *   L1 #FAFAFB  近白       输入框、次级卡片
 *   L2 #F4F4F6  极浅灰     页面背景
 *   L3 #EDEDF0  浅灰       分组底、按下态
 *   L4 #E2E2E7  灰         边框
 *   L5 #C8C8D0  中灰       占位、禁用
 *
 * 暗色阶梯（背景→卡片→更亮）
 *   D0 #17171A  页面背景
 *   D1 #1F1F23  卡片
 *   D2 #28282D  输入框、次级卡片
 *   D3 #34343A  浮层、Toast
 *   D4 #45454D  边框
 *   D5 #5A5A63  禁用
 */
object Palette {

    // ---- 浅色 ----
    private val L0 = Color(0xFFFFFFFF)
    private val L1 = Color(0xFFF8F8FA)
    private val L2 = Color(0xFFEEEEF2)
    private val L3 = Color(0xFFE6E6EB)
    private val L4 = Color(0xFFD8D8E0)
    private val L5 = Color(0xFFBEBEC8)

    private val LightInk = Color(0xFF1A1A1F)
    private val LightInkMuted = Color(0xFF5A5A66)

    // ---- 暗色 ----
    private val D0 = Color(0xFF141418)
    private val D1 = Color(0xFF2A2A30)
    private val D2 = Color(0xFF33333A)
    private val D3 = Color(0xFF45454E)
    private val D4 = Color(0xFF52525C)
    private val D5 = Color(0xFF68686F)

    private val DarkInk = Color(0xFFF5F5F7)
    private val DarkInkMuted = Color(0xFFAEAEB8)

    // ---- 语义色 ----
    private val Danger = Color(0xFFE5484D)
    private val DangerDark = Color(0xFFFF6369)

    fun light(palette: PaletteScheme): ColorScheme {
        val accent = palette.lightAccent()
        return lightColorScheme(
            primary = accent.primary,
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = accent.primaryContainer,
            onPrimaryContainer = accent.onPrimaryContainer,
            inversePrimary = accent.primary,
            secondary = accent.primary,
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = accent.primaryContainer,
            onSecondaryContainer = accent.onPrimaryContainer,
            tertiary = accent.primary,
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = accent.primaryContainer,
            onTertiaryContainer = accent.onPrimaryContainer,
            background = L2,
            onBackground = LightInk,
            surface = L0,
            onSurface = LightInk,
            surfaceVariant = L3,
            onSurfaceVariant = LightInkMuted,
            surfaceTint = accent.primary,
            inverseSurface = Color(0xFF2A2A30),
            inverseOnSurface = Color(0xFFF5F5F7),
            error = Danger,
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFBE4E4),
            onErrorContainer = Color(0xFF4A0F12),
            outline = L5,
            outlineVariant = L4,
            scrim = Color(0xFF000000),
            surfaceBright = L0,
            surfaceDim = L3,
            surfaceContainerLowest = L0,
            surfaceContainerLow = L1,
            surfaceContainer = L2,
            surfaceContainerHigh = L3,
            surfaceContainerHighest = L4,
        )
    }

    fun dark(palette: PaletteScheme): ColorScheme {
        val accent = palette.darkAccent()
        return darkColorScheme(
            primary = accent.primary,
            onPrimary = accent.onPrimary,
            primaryContainer = accent.primaryContainer,
            onPrimaryContainer = accent.onPrimaryContainer,
            inversePrimary = accent.primary,
            secondary = accent.primary,
            onSecondary = accent.onPrimary,
            secondaryContainer = accent.primaryContainer,
            onSecondaryContainer = accent.onPrimaryContainer,
            tertiary = accent.primary,
            onTertiary = accent.onPrimary,
            tertiaryContainer = accent.primaryContainer,
            onTertiaryContainer = accent.onPrimaryContainer,
            background = D0,
            onBackground = DarkInk,
            surface = D1,
            onSurface = DarkInk,
            surfaceVariant = D2,
            onSurfaceVariant = DarkInkMuted,
            surfaceTint = accent.primary,
            inverseSurface = Color(0xFFF2F2F4),
            inverseOnSurface = Color(0xFF202024),
            error = DangerDark,
            onError = Color(0xFF2A0A0C),
            errorContainer = Color(0xFF5C1A1D),
            onErrorContainer = Color(0xFFFFDAD9),
            outline = D5,
            outlineVariant = D4,
            scrim = Color(0xFF000000),
            surfaceBright = D3,
            surfaceDim = Color(0xFF121215),
            surfaceContainerLowest = Color(0xFF131316),
            surfaceContainerLow = D0,
            surfaceContainer = D2,
            surfaceContainerHigh = D3,
            surfaceContainerHighest = D4,
        )
    }

    /** 浮层（Toast、菜单）专用色：比卡片再亮一档，保证浮起来。 */
    fun overlay(dark: Boolean): Color {
        return if (dark) D3 else Color(0xFF2A2A30)
    }

    fun onOverlay(): Color {
        return Color(0xFFF5F5F7)
    }
}

/** 配色预览用的三色小样，直接从各方案的实际强调色取。 */
data class PalettePreview(
    val background: Color,
    val card: Color,
    val accent: Color,
)

fun PaletteScheme.preview(): PalettePreview {
    return PalettePreview(
        background = Color(0xFFF4F4F6),
        card = Color(0xFFFFFFFF),
        accent = lightAccent().primary,
    )
}
