package com.nova.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CenterToast(
    message: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = message != null,
            enter = fadeIn(tween(180)) + scaleIn(tween(220), initialScale = 0.92f),
            exit = fadeOut(tween(160)) + scaleOut(tween(180), targetScale = 0.96f),
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 48.dp)
                    .widthIn(max = 280.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                shadowElevation = 8.dp,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = message.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
