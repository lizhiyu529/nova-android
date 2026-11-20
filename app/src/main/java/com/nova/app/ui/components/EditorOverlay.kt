package com.nova.app.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.theme.NovaTheme

@Composable
fun EditorOverlay(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = NovaStrings.strings
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    var field by remember {
        mutableStateOf(TextFieldValue(text, TextRange(text.length.coerceAtLeast(0))))
    }

    BackHandler { onDismiss() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NovaTheme.colors.pageBackground)
            .then(
                Modifier.background(NovaTheme.colors.pageBackground),
            ),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = NovaTheme.colors.pageBackground,
            interactionSource = interactionSource,
            onClick = {},
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    CircleAction(
                        icon = NovaIcons.Close,
                        contentDescription = strings.cancel,
                        background = NovaTheme.colors.containerHigh,
                        foreground = NovaTheme.colors.textPrimary,
                        enabled = true,
                        onClick = {
                            onTextChange(field.text)
                            onDismiss()
                        },
                    )
                    Text(
                        text = strings.chatExpandTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = NovaTheme.colors.textPrimary,
                        textAlign = TextAlign.Center,
                    )
                    CircleAction(
                        icon = NovaIcons.Send,
                        contentDescription = strings.send,
                        background = NovaTheme.colors.accent,
                        foreground = NovaTheme.colors.onAccent,
                        enabled = field.text.isNotBlank(),
                        onClick = {
                            onTextChange(field.text)
                            onSend()
                        },
                    )
                }
                Spacer(Modifier.height(12.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .imePadding(),
                    shape = RoundedCornerShape(24.dp),
                    color = NovaTheme.colors.card,
                    border = BorderStroke(0.8.dp, NovaTheme.colors.border),
                ) {
                    BasicTextField(
                        value = field,
                        onValueChange = {
                            field = it
                            onTextChange(it.text)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                            .focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = NovaTheme.colors.textPrimary,
                        ),
                        cursorBrush = SolidColor(NovaTheme.colors.accent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                        keyboardActions = KeyboardActions(),
                        decorationBox = { innerTextField ->
                            if (field.text.isEmpty()) {
                                Text(
                                    text = strings.inputPlaceholder,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = NovaTheme.colors.textSecondary,
                                )
                            }
                            innerTextField()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CircleAction(
    icon: ImageVector,
    contentDescription: String,
    background: androidx.compose.ui.graphics.Color,
    foreground: androidx.compose.ui.graphics.Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
        Surface(
            onClick = { if (enabled) onClick() },
            enabled = enabled,
            shape = CircleShape,
            color = if (enabled) background else NovaTheme.colors.containerHigh,
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(16.dp),
                    tint = if (enabled) foreground else NovaTheme.colors.textDisabled,
                )
            }
        }
    }
}
