package com.nova.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nova.app.data.AssistantProfile
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.components.NovaSwitch
import com.nova.app.ui.theme.NovaTheme

@Composable
fun AssistantScreen(
    profile: AssistantProfile,
    onProfileChange: (AssistantProfile) -> Unit,
) {
    val strings = NovaStrings.strings
    var draftMemory by rememberSaveable { mutableStateOf("") }

    fun addMemory() {
        val value = draftMemory.trim()
        if (value.isEmpty()) return
        if (profile.memories.any { it.equals(value, ignoreCase = true) }) {
            draftMemory = ""
            return
        }
        onProfileChange(profile.copy(memories = profile.memories + value))
        draftMemory = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        SectionLabel(strings.promptSection)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = NovaTheme.colors.card,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = strings.promptEnable,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NovaTheme.colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    NovaSwitch(
                        checked = profile.promptEnabled,
                        onCheckedChange = { onProfileChange(profile.copy(promptEnabled = it)) },
                    )
                }
                HorizontalDivider(
                    thickness = 0.7.dp,
                    color = NovaTheme.colors.divider,
                )
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = NovaTheme.colors.containerHigh,
                        border = BorderStroke(0.8.dp, NovaTheme.colors.border),
                    ) {
                        BasicTextField(
                            value = profile.prompt,
                            onValueChange = { onProfileChange(profile.copy(prompt = it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 116.dp)
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = NovaTheme.colors.textPrimary,
                            ),
                            cursorBrush = SolidColor(NovaTheme.colors.accent),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                            keyboardActions = KeyboardActions(),
                            decorationBox = { innerTextField ->
                                if (profile.prompt.isEmpty()) {
                                    Text(
                                        text = strings.promptPlaceholder,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NovaTheme.colors.textSecondary,
                                    )
                                }
                                innerTextField()
                            },
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = strings.promptHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = NovaTheme.colors.textTertiary,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = strings.charCount(profile.prompt.length),
                            style = MaterialTheme.typography.bodySmall,
                            color = NovaTheme.colors.textTertiary,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        SectionLabel(strings.memorySection)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = NovaTheme.colors.card,
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = strings.memoryEnable,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NovaTheme.colors.textPrimary,
                        modifier = Modifier.weight(1f),
                    )
                    NovaSwitch(
                        checked = profile.memoryEnabled,
                        onCheckedChange = { onProfileChange(profile.copy(memoryEnabled = it)) },
                    )
                }
                HorizontalDivider(
                    thickness = 0.7.dp,
                    color = NovaTheme.colors.divider,
                )
                Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                    Text(
                        text = strings.memoryHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = NovaTheme.colors.textTertiary,
                    )
                    Spacer(Modifier.height(10.dp))
                    MemoryInput(
                        value = draftMemory,
                        onValueChange = { draftMemory = it },
                        onAdd = { addMemory() },
                    )
                    Spacer(Modifier.height(10.dp))
                    if (profile.memories.isEmpty()) {
                        Text(
                            text = strings.memoryEmpty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NovaTheme.colors.textTertiary,
                            modifier = Modifier.padding(vertical = 12.dp),
                        )
                    } else {
                        profile.memories.forEachIndexed { index, item ->
                            if (index > 0) {
                                HorizontalDivider(
                                    thickness = 0.7.dp,
                                    color = NovaTheme.colors.divider,
                                )
                            }
                            MemoryRow(
                                text = item,
                                onDelete = {
                                    onProfileChange(
                                        profile.copy(
                                            memories = profile.memories.filterIndexed { i, _ -> i != index },
                                        ),
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = NovaTheme.colors.textSecondary,
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp),
    )
}

@Composable
private fun MemoryInput(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
) {
    val strings = NovaStrings.strings
    val enabled = value.isNotBlank()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = NovaTheme.colors.containerHigh,
            border = BorderStroke(0.8.dp, NovaTheme.colors.border),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = NovaTheme.colors.textPrimary,
                ),
                cursorBrush = SolidColor(NovaTheme.colors.accent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = strings.memoryPlaceholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NovaTheme.colors.textSecondary,
                        )
                    }
                    innerTextField()
                },
            )
        }
        Spacer(Modifier.width(10.dp))
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Surface(
                onClick = { if (enabled) onAdd() },
                enabled = enabled,
                shape = CircleShape,
                color = if (enabled) NovaTheme.colors.accent else NovaTheme.colors.containerHigh,
            ) {
                Row(
                    modifier = Modifier.size(40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = NovaIcons.Plus,
                        contentDescription = strings.memoryAdd,
                        modifier = Modifier.size(16.dp),
                        tint = if (enabled) NovaTheme.colors.onAccent else NovaTheme.colors.textDisabled,
                    )
                }
            }
        }
    }
}

@Composable
private fun MemoryRow(text: String, onDelete: () -> Unit) {
    val strings = NovaStrings.strings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = NovaTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Surface(
                onClick = onDelete,
                shape = CircleShape,
                color = NovaTheme.colors.containerHigh,
            ) {
                Row(
                    modifier = Modifier.size(32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = NovaIcons.Close,
                        contentDescription = strings.memoryDelete,
                        modifier = Modifier.size(13.dp),
                        tint = NovaTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}
