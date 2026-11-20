package com.nova.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nova.app.ui.theme.NovaTheme
import com.nova.app.ui.theme.NovaTransitions

@Composable
fun CenterToast(
    message: String?,
    modifier: Modifier = Modifier,
) {
    var snapshot by remember { mutableStateOf("") }
    LaunchedEffect(message) {
        if (message != null) snapshot = message
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = message != null,
            enter = NovaTransitions.toastEnter,
            exit = NovaTransitions.toastExit,
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NovaTheme.colors.toastContainer,
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
                        color = NovaTheme.colors.toastContent,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
