package com.nova.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * 配色采用 iOS 系统灰阶（Apple Human Interface Guidelines 官方色值）。
 *
 * 浅色层次：
 *   页面背景 #FFFFFF（纯白）
 *   分组底色 #F2F2F7（systemGray6）
 *   卡片     #FFFFFF（白卡片浮在灰底上）
 *   输入框   #F2F2F7
 *   分割线   #C6C6C8（systemGray4）
 *
 * 暗色层次：
 *   页面背景 #000000（纯黑）
 *   分组底色 #000000
 *   卡片     #1C1C1E（systemGray6 dark）
 *   输入框   #2C2C2E（systemGray5 dark）
 *   分割线   #38383A（opaqueSeparator dark）
 *
 * 这套的层次逻辑：卡片永远比所在背景「亮」，靠系统灰阶的明度差区分，
 * 不依赖边框，也不依赖自己发明的色阶。
 */
object Palette {

    // ---------- iOS 系统灰阶 ----------
    private val Gray6Light = Color(0xFFF2F2F7)
    private val Gray5Light = Color(0xFFE5E5EA)
    private val Gray4Light = Color(0xFFD1D1D6)
    private val Gray3Light = Color(0xFFC7C7CC)
    private val SeparatorLight = Color(0xFFC6C6C8)

    private val Gray6Dark = Color(0xFF1C1C1E)
    private val Gray5Dark = Color(0xFF2C2C2E)
    private val Gray4Dark = Color(0xFF3A3A3C)
    private val Gray3Dark = Color(0xFF48484A)
    private val SeparatorDark = Color(0xFF38383A)

    // ---------- 文字 ----------
    private val LabelLight = Color(0xFF000000)
    private val SecondaryLabelLight = Color(0xFF6E6E73)
    private val LabelDark = Color(0xFFFFFFFF)
    private val SecondaryLabelDark = Color(0xFF8E8E93)

    // ---------- 强调色（iOS systemBlue）----------
    private val BlueLight = Color(0xFF007AFF)
    private val BlueDark = Color(0xFF0A84FF)

    // ---------- 语义色 ----------
    private val RedLight = Color(0xFFFF3B30)
    private val RedDark = Color(0xFFFF453A)
    private val GreenLight = Color(0xFF34C759)
    private val GreenDark = Color(0xFF30D158)

    val Light: ColorScheme = lightColorScheme(
        primary = BlueLight,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Gray6Light,
        onPrimaryContainer = LabelLight,
        secondary = SecondaryLabelLight,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Gray6Light,
        onSecondaryContainer = LabelLight,
        tertiary = BlueLight,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Gray6Light,
        onTertiaryContainer = LabelLight,
        error = RedLight,
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFE5E3),
        onErrorContainer = Color(0xFF8A1F18),
        background = Color(0xFFFFFFFF),
        onBackground = LabelLight,
        surface = Color(0xFFFFFFFF),
        onSurface = LabelLight,
        surfaceVariant = Gray6Light,
        onSurfaceVariant = SecondaryLabelLight,
        outline = SeparatorLight,
        outlineVariant = Gray4Light,
        scrim = Color(0xFF000000),
        inverseSurface = Gray6Dark,
        inverseOnSurface = LabelDark,
        inversePrimary = BlueDark,
        surfaceTint = Color(0xFFFFFFFF),
        surfaceDim = Gray6Light,
        surfaceBright = Color(0xFFFFFFFF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Gray6Light,
        surfaceContainer = Gray6Light,
        surfaceContainerHigh = Gray5Light,
        surfaceContainerHighest = Gray4Light,
    )

    val Dark: ColorScheme = darkColorScheme(
        primary = BlueDark,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Gray4Dark,
        onPrimaryContainer = LabelDark,
        secondary = SecondaryLabelDark,
        onSecondary = Color(0xFF000000),
        secondaryContainer = Gray6Dark,
        onSecondaryContainer = LabelDark,
        tertiary = BlueDark,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Gray4Dark,
        onTertiaryContainer = LabelDark,
        error = RedDark,
        onError = Color(0xFF000000),
        errorContainer = Color(0xFF5C1A17),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF000000),
        onBackground = LabelDark,
        surface = Color(0xFF000000),
        onSurface = LabelDark,
        surfaceVariant = Gray6Dark,
        onSurfaceVariant = SecondaryLabelDark,
        outline = SeparatorDark,
        outlineVariant = Gray4Dark,
        scrim = Color(0xFF000000),
        inverseSurface = Gray6Light,
        inverseOnSurface = LabelLight,
        inversePrimary = BlueLight,
        surfaceTint = Color(0xFF000000),
        surfaceDim = Color(0xFF000000),
        surfaceBright = Gray4Dark,
        surfaceContainerLowest = Color(0xFF000000),
        surfaceContainerLow = Gray6Dark,
        surfaceContainer = Gray6Dark,
        surfaceContainerHigh = Gray5Dark,
        surfaceContainerHighest = Gray4Dark,
    )

    /** 系统绿，用于开关等肯定状态。 */
    fun green(dark: Boolean): Color {
        return if (dark) GreenDark else GreenLight
    }

    /**
     * Toast / 浮层：iOS 用接近纯黑的深色胶囊 + 白字，
     * 两种主题下都用同一个深色，辨识度最高。
     */
    fun overlay(): Color {
        return Color(0xFF2C2C2E)
    }

    fun onOverlay(): Color {
        return Color(0xFFFFFFFF)
    }

    /** 卡片：比所在背景亮一档。浅色下是纯白，暗色下是 systemGray6。 */
    fun card(dark: Boolean): Color {
        return if (dark) Gray6Dark else Color(0xFFFFFFFF)
    }

    /** 页面/分组背景。 */
    fun pageBackground(dark: Boolean): Color {
        return if (dark) Color(0xFF000000) else Gray6Light
    }
}
