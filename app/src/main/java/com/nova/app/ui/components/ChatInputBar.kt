package com.nova.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.components.rememberPressScale
import com.nova.app.ui.theme.NovaMotion
import com.nova.app.ui.theme.NovaTheme

private val ButtonTouchTarget = 34.dp

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onStop: () -> Unit,
    streaming: Boolean,
    onAttach: () -> Unit,
    onExpand: () -> Unit,
    reasoningLabel: String,
    onReasoningClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = NovaTheme.colors.card,
        border = BorderStroke(0.8.dp, NovaTheme.colors.border),
    ) {
        Column(
            modifier = Modifier.padding(start = 18.dp, end = 10.dp, top = 16.dp, bottom = 12.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 38.dp, max = 132.dp)
                    .padding(end = 10.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = NovaTheme.colors.textPrimary,
                ),
                cursorBrush = SolidColor(NovaTheme.colors.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (value.isNotBlank()) onSend() }),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = NovaStrings.strings.inputPlaceholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = NovaTheme.colors.textSecondary,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Spacer(Modifier.size(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AttachButton(onClick = onAttach)
                    Spacer(Modifier.size(8.dp))
                    ReasoningButton(
                        label = reasoningLabel,
                        onClick = onReasoningClick,
                    )
                    if (value.isNotBlank()) {
                        Spacer(Modifier.size(8.dp))
                        ExpandButton(onClick = onExpand)
                    }
                }
                if (streaming) {
                    StopButton(onClick = onStop)
                } else {
                    SendButton(enabled = value.isNotBlank(), onClick = onSend)
                }
            }
        }
    }
}

@Composable
private fun AttachButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by rememberPressScale(pressed, NovaMotion.PressScaleTight)
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(ButtonTouchTarget)
                .scale(scale),
            shape = CircleShape,
            color = NovaTheme.colors.containerHigh,
            border = BorderStroke(0.8.dp, NovaTheme.colors.divider),
            interactionSource = interactionSource,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = NovaIcons.Plus,
                    contentDescription = NovaStrings.strings.addAttachment,
                    modifier = Modifier.size(16.dp),
                    tint = NovaTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun ReasoningButton(label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by rememberPressScale(pressed, NovaMotion.PressScaleTight)
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .height(ButtonTouchTarget)
                .scale(scale),
            shape = CircleShape,
            color = NovaTheme.colors.containerHigh,
            border = BorderStroke(0.8.dp, NovaTheme.colors.divider),
            interactionSource = interactionSource,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = AppIcons.Atom,
                    contentDescription = NovaStrings.strings.reasoningTitle,
                    modifier = Modifier.size(14.dp),
                    tint = NovaTheme.colors.textSecondary,
                )
                if (label.isNotBlank()) {
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = NovaTheme.colors.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by rememberPressScale(pressed, NovaMotion.PressScaleTight)
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(ButtonTouchTarget)
                .scale(scale),
            shape = CircleShape,
            color = NovaTheme.colors.containerHigh,
            border = BorderStroke(0.8.dp, NovaTheme.colors.divider),
            interactionSource = interactionSource,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = NovaIcons.Expand,
                    contentDescription = NovaStrings.strings.chatExpand,
                    modifier = Modifier.size(15.dp),
                    tint = NovaTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
private fun StopButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by rememberPressScale(pressed)
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(ButtonTouchTarget + 2.dp)
                .scale(scale),
            shape = CircleShape,
            color = NovaTheme.colors.accent,
            interactionSource = interactionSource,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .background(NovaTheme.colors.onAccent, RoundedCornerShape(3.dp)),
                )
            }
        }
    }
}

@Composable
private fun SendButton(enabled: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by rememberPressScale(pressed)
    val container by animateColorAsState(
        targetValue = if (enabled) {
            NovaTheme.colors.accent
        } else {
            NovaTheme.colors.containerHigh
        },
        animationSpec = NovaMotion.fade(),
        label = "sendContainer",
    )
    val content by animateColorAsState(
        targetValue = if (enabled) {
            NovaTheme.colors.onAccent
        } else {
            NovaTheme.colors.textDisabled
        },
        animationSpec = NovaMotion.fade(),
        label = "sendContent",
    )
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = { if (enabled) onClick() },
            enabled = enabled,
            modifier = Modifier
                .size(ButtonTouchTarget + 2.dp)
                .scale(scale),
            shape = CircleShape,
            color = container,
            border = if (enabled) {
                null
            } else {
                BorderStroke(0.8.dp, NovaTheme.colors.divider)
            },
            interactionSource = interactionSource,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = NovaIcons.Send,
                    contentDescription = NovaStrings.strings.send,
                    modifier = Modifier.size(17.dp),
                    tint = content,
                )
            }
        }
    }
}
