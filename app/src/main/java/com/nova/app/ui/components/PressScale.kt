package com.nova.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.nova.app.ui.theme.NovaMotion

@Composable
fun rememberPressScale(
    pressed: Boolean,
    pressedScale: Float = NovaMotion.PressScale,
): State<Float> = animateFloatAsState(
    targetValue = if (pressed) pressedScale else 1f,
    animationSpec = NovaMotion.press(),
    label = "pressScale",
)
