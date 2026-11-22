package com.nova.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nova.app.data.chat.MessagePart
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.theme.NovaMotion
import com.nova.app.ui.theme.NovaTheme
import com.nova.app.ui.theme.NovaTransitions

@Composable
fun ReasoningPanel(
    text: String,
    expanded: Boolean,
    running: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (text.isBlank()) return
    val strings = NovaStrings.strings
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = NovaMotion.press(),
        label = "reasoningAngle",
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(16.dp),
        color = NovaTheme.colors.containerLow,
        border = BorderStroke(0.8.dp, NovaTheme.colors.divider),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 13.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(18.dp),
                    shape = CircleShape,
                    color = NovaTheme.colors.accentSubtle,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = AppIcons.Atom,
                            contentDescription = null,
                            modifier = Modifier.size(11.dp),
                            tint = NovaTheme.colors.onAccentSubtle,
                        )
                    }
                }
                Spacer(Modifier.width(9.dp))
                Text(
                    text = strings.reasoningPanelTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = NovaTheme.colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
                if (running) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 1.6.dp,
                        color = NovaTheme.colors.textTertiary,
                    )
                    Spacer(Modifier.width(6.dp))
                }
                Icon(
                    imageVector = NovaIcons.ChevronDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(14.dp)
                        .rotate(rotation),
                    tint = NovaTheme.colors.textTertiary,
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = NovaTransitions.sectionEnter,
                exit = NovaTransitions.sectionExit,
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 13.dp)
                            .heightIn(min = 0.7.dp)
                            .background(NovaTheme.colors.divider),
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                        ),
                        color = NovaTheme.colors.textTertiary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 13.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ToolRunPanel(
    part: MessagePart.Tool,
    modifier: Modifier = Modifier,
) {
    val strings = NovaStrings.strings
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = NovaMotion.press(),
        label = "toolAngle",
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(16.dp),
        color = NovaTheme.colors.containerLow,
        border = BorderStroke(
            0.8.dp,
            if (!part.isExecuted) NovaTheme.colors.accent else NovaTheme.colors.divider,
        ),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = part.isExecuted) { expanded = !expanded }
                    .padding(horizontal = 13.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!part.isExecuted) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(15.dp),
                        strokeWidth = 1.8.dp,
                        color = NovaTheme.colors.accent,
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(15.dp),
                        shape = CircleShape,
                        color = NovaTheme.colors.accentSubtle,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = NovaIcons.Check,
                                contentDescription = null,
                                modifier = Modifier.size(9.dp),
                                tint = NovaTheme.colors.onAccentSubtle,
                            )
                        }
                    }
                }
                Spacer(Modifier.width(9.dp))
                Text(
                    text = part.toolName,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = FontFamily.Monospace,
                    ),
                    color = NovaTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (part.isExecuted) {
                    Icon(
                        imageVector = NovaIcons.ChevronDown,
                        contentDescription = null,
                        modifier = Modifier
                            .size(14.dp)
                            .rotate(rotation),
                        tint = NovaTheme.colors.textTertiary,
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded && part.isExecuted,
                enter = NovaTransitions.sectionEnter,
                exit = NovaTransitions.sectionExit,
            ) {
                Column(modifier = Modifier.padding(start = 13.dp, end = 13.dp, bottom = 10.dp)) {
                    JsonBlock(label = strings.toolCallArguments, content = part.input)
                    Spacer(Modifier.size(6.dp))
                    JsonBlock(label = strings.toolCallResult, content = part.output)
                }
            }
        }
    }
}

@Composable
private fun JsonBlock(label: String, content: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = NovaTheme.colors.textTertiary,
        )
        Spacer(Modifier.size(3.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(11.dp),
            color = NovaTheme.colors.inputBackground,
            border = BorderStroke(0.7.dp, NovaTheme.colors.divider),
        ) {
            Text(
                text = prettyJson(content),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                ),
                color = NovaTheme.colors.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 160.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 11.dp, vertical = 9.dp),
            )
        }
    }
}

private fun prettyJson(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return "{}"
    val looksObject = trimmed.startsWith("{")
    val looksArray = trimmed.startsWith("[")
    if (!looksObject && !looksArray) return trimmed
    return runCatching {
        if (looksObject) org.json.JSONObject(trimmed).toString(2)
        else org.json.JSONArray(trimmed).toString(2)
    }.getOrDefault(trimmed)
}
