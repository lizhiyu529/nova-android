package com.nova.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 居中提示。用 message 的「上一帧快照」渲染，
 * 保证退出动画期间仍有内容，不会露出空气泡。
 */
@Composable
fun CenterToast(
    message: String?,
    modifier: Modifier = Modifier,
) {
    var snapshot by remember { mutableStateOf("") }
    if (message != null && message != snapshot) {
        snapshot = message
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = message != null,
            enter = fadeIn(tween(160)) +
                scaleIn(tween(260, easing = LinearOutSlowInEasing), initialScale = 0.88f) +
                slideInVertically(tween(260, easing = LinearOutSlowInEasing)) { it / 3 },
            exit = fadeOut(tween(200)) +
                slideOutVertically(tween(220)) { it / 4 },
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = toastContainer(),
                shadowElevation = 6.dp,
            ) {
                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .padding(horizontal = 18.dp, vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = snapshot,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/** 暗色下用深灰卡片而不是反色（反色会变白）。 */
@Composable
private fun toastContainer(): Color {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (dark) Color(0xFF3A3A3D) else Color(0xFF2F3036)
}

@Composable
private fun toastContent(): Color {
    return Color(0xFFF2F2F5)
}

