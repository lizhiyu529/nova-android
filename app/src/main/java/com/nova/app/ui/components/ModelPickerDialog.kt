package com.nova.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.theme.NovaTheme

@Composable
fun ModelPickerDialog(
    models: List<String>,
    alreadyAdded: Set<String>,
    loading: Boolean,
    onImport: (List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = NovaStrings.strings
    val windowHeight = LocalConfiguration.current.screenHeightDp
    val listHeight = (windowHeight * 0.46f).dp.coerceIn(200.dp, 360.dp)
    val windowWidth = LocalConfiguration.current.screenWidthDp
    val horizontalPadding = (windowWidth * 0.09f).dp.coerceIn(18.dp, 36.dp)

    var selected by remember(models) {
        mutableStateOf(models.filterNot { it in alreadyAdded }.toSet())
    }

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
                        text = strings.modelsPick,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NovaTheme.colors.textPrimary,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (loading) {
                            strings.modelsLoading
                        } else {
                            strings.modelsSelected(selected.size) + " · " + strings.modelsCount(models.size)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = NovaTheme.colors.textTertiary,
                    )
                }

                if (!loading && models.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        TinyChip(
                            text = strings.modelsAll,
                            onClick = { selected = models.toSet() },
                        )
                        TinyChip(
                            text = strings.modelsNone,
                            onClick = { selected = emptySet() },
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                }

                HorizontalDivider(thickness = 0.7.dp, color = NovaTheme.colors.divider)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = listHeight),
                    color = androidx.compose.ui.graphics.Color.Transparent,
                ) {
                    if (loading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = strings.modelsLoading,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NovaTheme.colors.textSecondary,
                            )
                        }
                    } else if (models.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = strings.providerModelsEmpty,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NovaTheme.colors.textTertiary,
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(models, key = { it }) { id ->
                                val checked = id in selected
                                val added = id in alreadyAdded
                                PickerRow(
                                    id = id,
                                    checked = checked,
                                    added = added,
                                    onToggle = {
                                        if (!added) {
                                            selected = if (checked) {
                                                selected - id
                                            } else {
                                                selected + id
                                            }
                                        }
                                    },
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 0.7.dp, color = NovaTheme.colors.divider)
                Row(modifier = Modifier.fillMaxWidth()) {
                    DialogAction(
                        text = strings.cancel,
                        color = NovaTheme.colors.textSecondary,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .width(0.7.dp)
                            .height(50.dp)
                            .background(NovaTheme.colors.divider),
                    )
                    DialogAction(
                        text = strings.modelsImport,
                        color = NovaTheme.colors.accent,
                        enabled = selected.isNotEmpty() && !loading,
                        onClick = { onImport(selected.toList()) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun PickerRow(
    id: String,
    checked: Boolean,
    added: Boolean,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !added, onClick = onToggle)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = id,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
            ),
            color = if (added) NovaTheme.colors.textDisabled else NovaTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.size(10.dp))
        Surface(
            modifier = Modifier.size(20.dp),
            shape = CircleShape,
            color = when {
                added -> NovaTheme.colors.containerHigh
                checked -> NovaTheme.colors.accent
                else -> androidx.compose.ui.graphics.Color.Transparent
            },
            border = if (added || checked) {
                null
            } else {
                androidx.compose.foundation.BorderStroke(1.5.dp, NovaTheme.colors.border)
            },
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (checked && !added) {
                    Icon(
                        imageVector = NovaIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = NovaTheme.colors.onAccent,
                    )
                } else if (added) {
                    Icon(
                        imageVector = NovaIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(11.dp),
                        tint = NovaTheme.colors.textDisabled,
                    )
                }
            }
        }
    }
}

@Composable
private fun TinyChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(9.dp),
        color = NovaTheme.colors.containerHigh,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = NovaTheme.colors.textSecondary,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun DialogAction(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = { if (enabled) onClick() },
        modifier = modifier.height(50.dp),
        color = androidx.compose.ui.graphics.Color.Transparent,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = if (enabled) color else NovaTheme.colors.textDisabled,
            )
        }
    }
}
