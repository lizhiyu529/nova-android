package com.nova.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nova.app.data.provider.ReasoningEffort
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.theme.NovaTheme

@Composable
fun ReasoningPickerDialog(
    current: ReasoningEffort,
    onSelect: (ReasoningEffort) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = NovaStrings.strings
    val windowWidth = LocalConfiguration.current.screenWidthDp
    val horizontalPadding = (windowWidth * 0.11f).dp.coerceIn(20.dp, 42.dp)

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
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = strings.reasoningTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NovaTheme.colors.textPrimary,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = strings.reasoningHint,
                        style = MaterialTheme.typography.labelSmall,
                        color = NovaTheme.colors.textTertiary,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(thickness = 0.7.dp, color = NovaTheme.colors.divider)
                ReasoningEffort.entries.forEach { effort ->
                    ReasoningRow(
                        label = strings.reasoningLabel(effort.key),
                        selected = effort == current,
                        onClick = { onSelect(effort) },
                    )
                }
                HorizontalDivider(thickness = 0.7.dp, color = NovaTheme.colors.divider)
                Surface(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(0.dp),
                    color = androidx.compose.ui.graphics.Color.Transparent,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = strings.cancel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = NovaTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReasoningRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) NovaTheme.colors.accent else NovaTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(10.dp))
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(NovaTheme.colors.accent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = NovaIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = NovaTheme.colors.onAccent,
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            androidx.compose.ui.graphics.Color.Transparent,
                            CircleShape,
                        ),
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(1.dp),
                    ) {
                        Surface(
                            modifier = Modifier.size(18.dp),
                            shape = CircleShape,
                            color = androidx.compose.ui.graphics.Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                NovaTheme.colors.border,
                            ),
                        ) {}
                    }
                }
            }
        }
    }
}
