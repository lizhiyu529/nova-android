package com.nova.app.ui.theme

import androidx.compose.ui.graphics.Color

/** 配色方案：只决定强调色，灰阶由 Palette 统一提供。 */
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
    val secondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
)

data class DarkAccent(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
)

fun PaletteScheme.lightAccent(): Accent = when (this) {
    PaletteScheme.NeutralGray -> Accent(
        primary = Color(0xFF4A4A55),
        primaryContainer = Color(0xFFE9E9EE),
        onPrimaryContainer = Color(0xFF1A1A1F),
        secondary = Color(0xFF565A61),
        secondaryContainer = Color(0xFFEFF0F2),
        onSecondaryContainer = Color(0xFF3B3E44),
        tertiary = Color(0xFF6E6E78),
        tertiaryContainer = Color(0xFFF0F0F4),
        onTertiaryContainer = Color(0xFF2A2A32),
    )
    PaletteScheme.NeutralBlack -> Accent(
        primary = Color(0xFF1F1F24),
        primaryContainer = Color(0xFFE6E6E8),
        onPrimaryContainer = Color(0xFF141418),
        secondary = Color(0xFF4A4A50),
        secondaryContainer = Color(0xFFEDEDEF),
        onSecondaryContainer = Color(0xFF2C2C32),
        tertiary = Color(0xFF5A5A62),
        tertiaryContainer = Color(0xFFF0F0F2),
        onTertiaryContainer = Color(0xFF262629),
    )
    PaletteScheme.SilverGray -> Accent(
        primary = Color(0xFF5B7284),
        primaryContainer = Color(0xFFE4EBF1),
        onPrimaryContainer = Color(0xFF16232C),
        secondary = Color(0xFF697A87),
        secondaryContainer = Color(0xFFEBF0F4),
        onSecondaryContainer = Color(0xFF1E2A32),
        tertiary = Color(0xFF7A8794),
        tertiaryContainer = Color(0xFFEEF2F5),
        onTertiaryContainer = Color(0xFF232D35),
    )
    PaletteScheme.Caramel -> Accent(
        primary = Color(0xFFB0733F),
        primaryContainer = Color(0xFFF8E8D8),
        onPrimaryContainer = Color(0xFF3A2310),
        secondary = Color(0xFF8A6A4E),
        secondaryContainer = Color(0xFFF5EAE0),
        onSecondaryContainer = Color(0xFF2E2015),
        tertiary = Color(0xFFA08258),
        tertiaryContainer = Color(0xFFF7EFDF),
        onTertiaryContainer = Color(0xFF33280F),
    )
    PaletteScheme.Pudding -> Accent(
        primary = Color(0xFFAE8A1F),
        primaryContainer = Color(0xFFFAF0CE),
        onPrimaryContainer = Color(0xFF382C05),
        secondary = Color(0xFF8D7A3C),
        secondaryContainer = Color(0xFFF7F1DA),
        onSecondaryContainer = Color(0xFF2C2610),
        tertiary = Color(0xFF9E8748),
        tertiaryContainer = Color(0xFFF8F2DE),
        onTertiaryContainer = Color(0xFF2F2912),
    )
}

fun PaletteScheme.darkAccent(): DarkAccent = when (this) {
    PaletteScheme.NeutralGray -> DarkAccent(
        primary = Color(0xFFC9C9D2),
        onPrimary = Color(0xFF1E1E24),
        primaryContainer = Color(0xFF38383F),
        onPrimaryContainer = Color(0xFFE9E9EE),
        secondary = Color(0xFFC3C6CC),
        secondaryContainer = Color(0xFF3A3D43),
        onSecondaryContainer = Color(0xFFEFF0F2),
        tertiary = Color(0xFFCFCFD7),
        onTertiary = Color(0xFF232328),
        tertiaryContainer = Color(0xFF3E3E46),
        onTertiaryContainer = Color(0xFFF0F0F4),
    )
    PaletteScheme.NeutralBlack -> DarkAccent(
        primary = Color(0xFFE6E6E8),
        onPrimary = Color(0xFF1A1A1E),
        primaryContainer = Color(0xFF303036),
        onPrimaryContainer = Color(0xFFE6E6E8),
        secondary = Color(0xFFCFCFD4),
        secondaryContainer = Color(0xFF37373D),
        onSecondaryContainer = Color(0xFFEDEDEF),
        tertiary = Color(0xFFD8D8DD),
        onTertiary = Color(0xFF1E1E22),
        tertiaryContainer = Color(0xFF3C3C42),
        onTertiaryContainer = Color(0xFFF0F0F2),
    )
    PaletteScheme.SilverGray -> DarkAccent(
        primary = Color(0xFFA8C0D2),
        onPrimary = Color(0xFF17272F),
        primaryContainer = Color(0xFF32454F),
        onPrimaryContainer = Color(0xFFD9E6EF),
        secondary = Color(0xFFB4C3CE),
        secondaryContainer = Color(0xFF354852),
        onSecondaryContainer = Color(0xFFEBF0F4),
        tertiary = Color(0xFFBCC9D4),
        onTertiary = Color(0xFF1D2B33),
        tertiaryContainer = Color(0xFF3A4A54),
        onTertiaryContainer = Color(0xFFEEF2F5),
    )
    PaletteScheme.Caramel -> DarkAccent(
        primary = Color(0xFFE8BE92),
        onPrimary = Color(0xFF3A2310),
        primaryContainer = Color(0xFF553D28),
        onPrimaryContainer = Color(0xFFF8E8D8),
        secondary = Color(0xFFD8C2AC),
        secondaryContainer = Color(0xFF4A382A),
        onSecondaryContainer = Color(0xFFF5EAE0),
        tertiary = Color(0xFFD9C69E),
        tertiaryContainer = Color(0xFF473B26),
        onTertiaryContainer = Color(0xFFF7EFDF),
    )
    PaletteScheme.Pudding -> DarkAccent(
        primary = Color(0xFFE7D28C),
        onPrimary = Color(0xFF382C05),
        primaryContainer = Color(0xFF514212),
        onPrimaryContainer = Color(0xFFFAF0CE),
        secondary = Color(0xFFD9CD93),
        secondaryContainer = Color(0xFF453E20),
        onSecondaryContainer = Color(0xFFF7F1DA),
        tertiary = Color(0xFFD8C99C),
        tertiaryContainer = Color(0xFF443D24),
        onTertiaryContainer = Color(0xFFF8F2DE),
    )
}
