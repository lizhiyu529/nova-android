package com.nova.app.ui.theme

import androidx.compose.ui.graphics.Color

/** 配色方案：只决定「强调色」，灰阶统一由 Palette 提供。 */
enum class PaletteScheme(val label: String) {
    NeutralGray("中性灰"),
    NeutralBlack("中性黑"),
    SilverGray("银灰"),
    Caramel("焦糖"),
    Pudding("布丁"),
}

data class Accent(
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
)

data class DarkAccent(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
)

fun PaletteScheme.lightAccent(): Accent = when (this) {
    PaletteScheme.NeutralGray -> Accent(
        primary = Color(0xFF4A4A55),
        primaryContainer = Color(0xFFE8E8ED),
        onPrimaryContainer = Color(0xFF1A1A1F),
    )
    PaletteScheme.NeutralBlack -> Accent(
        primary = Color(0xFF1F1F24),
        primaryContainer = Color(0xFFE4E4E9),
        onPrimaryContainer = Color(0xFF141418),
    )
    PaletteScheme.SilverGray -> Accent(
        primary = Color(0xFF6B7A87),
        primaryContainer = Color(0xFFE1E8EE),
        onPrimaryContainer = Color(0xFF1B242B),
    )
    PaletteScheme.Caramel -> Accent(
        primary = Color(0xFFB07A46),
        primaryContainer = Color(0xFFF6E7D6),
        onPrimaryContainer = Color(0xFF3B2410),
    )
    PaletteScheme.Pudding -> Accent(
        primary = Color(0xFFB8912A),
        primaryContainer = Color(0xFFF9EFC9),
        onPrimaryContainer = Color(0xFF3A2E06),
    )
}

fun PaletteScheme.darkAccent(): DarkAccent = when (this) {
    PaletteScheme.NeutralGray -> DarkAccent(
        primary = Color(0xFFC9C9D2),
        onPrimary = Color(0xFF202026),
        primaryContainer = Color(0xFF3A3A42),
        onPrimaryContainer = Color(0xFFE8E8ED),
    )
    PaletteScheme.NeutralBlack -> DarkAccent(
        primary = Color(0xFFE2E2E8),
        onPrimary = Color(0xFF1A1A1F),
        primaryContainer = Color(0xFF33333A),
        onPrimaryContainer = Color(0xFFE4E4E9),
    )
    PaletteScheme.SilverGray -> DarkAccent(
        primary = Color(0xFFAEC1CE),
        onPrimary = Color(0xFF1C262D),
        primaryContainer = Color(0xFF39464F),
        onPrimaryContainer = Color(0xFFE1E8EE),
    )
    PaletteScheme.Caramel -> DarkAccent(
        primary = Color(0xFFE8BE92),
        onPrimary = Color(0xFF3B2410),
        primaryContainer = Color(0xFF54402C),
        onPrimaryContainer = Color(0xFFF6E7D6),
    )
    PaletteScheme.Pudding -> DarkAccent(
        primary = Color(0xFFE7D08A),
        onPrimary = Color(0xFF3A2E06),
        primaryContainer = Color(0xFF544617),
        onPrimaryContainer = Color(0xFFF9EFC9),
    )
}
