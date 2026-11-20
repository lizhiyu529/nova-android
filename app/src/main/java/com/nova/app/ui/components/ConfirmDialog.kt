package com.nova.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nova.app.ui.theme.NovaTheme

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val windowWidth = LocalConfiguration.current.screenWidthDp
    val horizontalPadding = (windowWidth * 0.115f).dp.coerceIn(20.dp, 44.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            shape = RoundedCornerShape(26.dp),
            color = NovaTheme.colors.dialog,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NovaTheme.colors.textPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NovaTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(22.dp))
                Surface(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(23.dp),
                    color = NovaTheme.colors.accent,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = confirmText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = NovaTheme.colors.onAccent,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Surface(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = NovaTheme.colors.containerHigh,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.height(11.dp))
                        Text(
                            text = dismissText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = NovaTheme.colors.textPrimary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
