package com.nova.app.ui.theme

import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

/**
 * 字号缩放通过覆盖 LocalDensity.fontScale 实现：
 * 所有以 sp 声明的尺寸（包括组件里写死的）都会等比例放大缩小，
 * 不需要在每个页面手动乘系数，也不会漏掉某个元素。
 */
@Composable
fun NovaTheme(
    darkTheme: Boolean,
    fontScale: Float = 1f,
    palette: PaletteScheme = PaletteScheme.NeutralGray,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val scaledDensity = if (fontScale == 1f) {
        density
    } else {
        Density(density.density, fontScale)
    }
    CompositionLocalProvider(LocalDensity provides scaledDensity) {
        MaterialExpressiveTheme(
            colorScheme = if (darkTheme) darkSchemeFor(palette) else lightSchemeFor(palette),
            motionScheme = MotionScheme.expressive(),
            shapes = NovaShapes,
            typography = NovaTypography,
            content = content,
        )
    }
}
