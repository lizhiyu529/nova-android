package com.nova.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nova.app.data.AssistantProfile
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.components.NovaSwitch
import com.nova.app.ui.theme.NovaMotion
import com.nova.app.ui.theme.NovaTheme
import com.nova.app.ui.theme.NovaTransitions

@Composable
fun AssistantScreen(
    profile: AssistantProfile,
    onProfileChange: (AssistantProfile) -> Unit,
    onOpenPromptEditor: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = NovaStrings.strings
    var memoryDraft by rememberSaveable { mutableStateOf("") }
    var memoryComposerOpen by rememberSaveable { mutableStateOf(false) }

    fun addMemory() {
        val value = memoryDraft.trim()
        if (value.isEmpty()) return
        if (profile.memories.none { it.equals(value, ignoreCase = true) }) {
            onProfileChange(profile.copy(memories = profile.memories + value))
        }
        memoryDraft = ""
        memoryComposerOpen = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        SectionTitle(strings.promptSection)
        AssistantCard {
            ToggleRow(
                title = strings.promptEnable,
                checked = profile.promptEnabled,
                onCheckedChange = { onProfileChange(profile.copy(promptEnabled = it)) },
            )
            AnimatedVisibility(
                visible = profile.promptEnabled,
                enter = NovaTransitions.sectionEnter,
                exit = NovaTransitions.sectionExit,
            ) {
                Column {
                    HorizontalDivider(thickness = 0.7.dp, color = NovaTheme.colors.divider)
                    PromptPreview(
                        text = profile.prompt,
                        onOpen = onOpenPromptEditor,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        SectionTitle(strings.memorySection)
        AssistantCard {
            ToggleRow(
                title = strings.memoryEnable,
                checked = profile.memoryEnabled,
                onCheckedChange = { onProfileChange(profile.copy(memoryEnabled = it)) },
            )
            AnimatedVisibility(
                visible = profile.memoryEnabled,
                enter = NovaTransitions.sectionEnter,
                exit = NovaTransitions.sectionExit,
            ) {
                Column {
                    HorizontalDivider(thickness = 0.7.dp, color = NovaTheme.colors.divider)
                    MemoryBlock(
                        memories = profile.memories,
                        composerOpen = memoryComposerOpen,
                        draft = memoryDraft,
                        onDraftChange = { memoryDraft = it },
                        onToggleComposer = { memoryComposerOpen = !memoryComposerOpen },
                        onAdd = { addMemory() },
                        onDelete = { index ->
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
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
fun PromptEditorScreen(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = NovaStrings.strings
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(22.dp),
            color = NovaTheme.colors.card,
            border = BorderStroke(0.8.dp, NovaTheme.colors.border),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = NovaTheme.colors.textPrimary,
                ),
                cursorBrush = SolidColor(NovaTheme.colors.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                keyboardActions = KeyboardActions(),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = strings.promptPlaceholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = NovaTheme.colors.textSecondary,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }
        Spacer(Modifier.height(10.dp))
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
                text = strings.charCount(value.length),
                style = MaterialTheme.typography.bodySmall,
                color = NovaTheme.colors.textTertiary,
            )
        }
    }
}

@Composable
private fun AssistantCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NovaTheme.colors.card,
    ) {
        Column { content() }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = NovaTheme.colors.textSecondary,
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp),
    )
}

@Composable
private fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = NovaTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        NovaSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PromptPreview(
    text: String,
    onOpen: () -> Unit,
) {
    val strings = NovaStrings.strings
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (text.isBlank()) strings.promptPreviewEmpty else text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (text.isBlank()) {
                NovaTheme.colors.textTertiary
            } else {
                NovaTheme.colors.textPrimary
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = strings.promptTapHint,
            style = MaterialTheme.typography.bodySmall,
            color = NovaTheme.colors.textTertiary,
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = NovaIcons.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = NovaTheme.colors.textTertiary,
        )
    }
}

@Composable
private fun MemoryBlock(
    memories: List<String>,
    composerOpen: Boolean,
    draft: String,
    onDraftChange: (String) -> Unit,
    onToggleComposer: () -> Unit,
    onAdd: () -> Unit,
    onDelete: (Int) -> Unit,
) {
    val strings = NovaStrings.strings
    val arrowRotation by animateFloatAsState(
        targetValue = if (composerOpen) 45f else 0f,
        animationSpec = NovaMotion.press(),
        label = "memoryComposerArrow",
    )

    Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
        Text(
            text = strings.memoryHint,
            style = MaterialTheme.typography.bodySmall,
            color = NovaTheme.colors.textTertiary,
        )
        Spacer(Modifier.height(10.dp))

        if (memories.isEmpty() && !composerOpen) {
            Text(
                text = strings.memoryEmpty,
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTheme.colors.textTertiary,
                modifier = Modifier.padding(vertical = 10.dp),
            )
        }

        memories.forEachIndexed { index, item ->
            if (index > 0) {
                HorizontalDivider(thickness = 0.7.dp, color = NovaTheme.colors.divider)
            }
            MemoryRow(text = item, onDelete = { onDelete(index) })
        }

        AnimatedVisibility(
            visible = composerOpen,
            enter = NovaTransitions.sectionEnter,
            exit = NovaTransitions.sectionExit,
        ) {
            Column {
                Spacer(Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = NovaTheme.colors.containerHigh,
                    border = BorderStroke(0.8.dp, NovaTheme.colors.border),
                ) {
                    BasicTextField(
                        value = draft,
                        onValueChange = onDraftChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = NovaTheme.colors.textPrimary,
                        ),
                        cursorBrush = SolidColor(NovaTheme.colors.accent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onAdd() }),
                        decorationBox = { innerTextField ->
                            if (draft.isEmpty()) {
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
                Spacer(Modifier.height(8.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleComposer)
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = NovaIcons.Plus,
                contentDescription = null,
                modifier = Modifier
                    .size(15.dp)
                    .rotate(arrowRotation),
                tint = NovaTheme.colors.accent,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = strings.memoryAddAction,
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTheme.colors.accent,
                modifier = Modifier.weight(1f),
            )
            if (composerOpen && draft.isNotBlank()) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                    Surface(
                        onClick = onAdd,
                        shape = CircleShape,
                        color = NovaTheme.colors.accent,
                    ) {
                        Row(
                            modifier = Modifier.size(30.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = NovaIcons.Check,
                                contentDescription = strings.memoryAdd,
                                modifier = Modifier.size(13.dp),
                                tint = NovaTheme.colors.onAccent,
                            )
                        }
                    }
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
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = NovaTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(10.dp))
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Surface(
                onClick = onDelete,
                shape = CircleShape,
                color = NovaTheme.colors.containerHigh,
            ) {
                Row(
                    modifier = Modifier.size(28.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = NovaIcons.Close,
                        contentDescription = strings.memoryDelete,
                        modifier = Modifier.size(12.dp),
                        tint = NovaTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}
