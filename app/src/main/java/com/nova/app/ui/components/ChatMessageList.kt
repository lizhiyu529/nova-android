package com.nova.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nova.app.data.chat.MessagePart
import com.nova.app.data.chat.MessageRole
import com.nova.app.data.chat.UIMessage
import com.nova.app.data.chat.isEmptyUIMessage
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.theme.NovaTheme

@Composable
fun ChatMessageList(
    messages: List<UIMessage>,
    loading: Boolean,
    errorText: String,
    reasoningExpanded: Boolean,
    onReasoningToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val visible = remember(messages) {
        messages.filter { message ->
            when (message.role) {
                MessageRole.USER -> message.toText().isNotBlank()
                MessageRole.ASSISTANT -> !message.parts.isEmptyUIMessage()
                else -> false
            }
        }
    }
    val total = visible.size + if (loading || errorText.isNotEmpty()) 1 else 0
    val signature = visible.hashCode() * 31 + total

    AutoScrollToBottom(
        listState = listState,
        itemCount = total,
        contentSignature = signature,
    )

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(visible) { index, message ->
            key("msg-$index-${message.role.value}") {
                when (message.role) {
                    MessageRole.USER -> UserBubble(message.toText())
                    MessageRole.ASSISTANT -> AssistantBlock(
                        parts = message.parts,
                        streaming = !message.finished && index == visible.lastIndex,
                        expanded = reasoningExpanded,
                        onToggle = onReasoningToggle,
                    )

                    else -> Unit
                }
            }
        }

        if (loading || errorText.isNotEmpty()) {
            item(key = "tail-live") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (errorText.isNotEmpty()) {
                        ErrorBlock(errorText)
                        Spacer(Modifier.height(6.dp))
                    }
                    if (loading) {
                        TypingDots(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }

        item(key = "tail-space") {
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
fun AssistantBlock(
    parts: List<MessagePart>,
    streaming: Boolean,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        var reasoningSeen = 0
        var textSeen = 0
        var toolSeen = 0
        parts.forEach { part ->
            val stableKey = when (part) {
                is MessagePart.Reasoning -> "r${reasoningSeen++}"
                is MessagePart.Text -> "t${textSeen++}"
                is MessagePart.Tool -> "x${toolSeen++}-${part.toolCallId}"
            }
            key(stableKey) {
                when (part) {
                    is MessagePart.Reasoning -> {
                        if (part.text.isNotBlank()) {
                            ReasoningPanel(
                                text = part.text,
                                expanded = expanded,
                                running = streaming && !part.finished,
                                onToggle = onToggle,
                            )
                        }
                    }

                    is MessagePart.Tool -> ToolRunPanel(part = part)

                    is MessagePart.Text -> {
                        if (part.text.isNotBlank()) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                MarkdownView(
                                    text = part.text,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(20.dp, 20.dp, 7.dp, 20.dp),
            color = NovaTheme.colors.accent,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = NovaTheme.colors.onAccent,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun ErrorBlock(message: String) {
    val strings = NovaStrings.strings
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 330.dp),
            shape = RoundedCornerShape(16.dp),
            color = NovaTheme.colors.containerLow,
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Text(
                    text = strings.errorTitle,
                    style = MaterialTheme.typography.labelLarge,
                    color = NovaTheme.colors.danger,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = NovaTheme.colors.textSecondary,
                )
            }
        }
    }
}

@Composable
fun EmptyChatState(modifier: Modifier = Modifier) {
    val strings = NovaStrings.strings
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = strings.chatEmptyTitle,
            style = MaterialTheme.typography.titleMedium,
            color = NovaTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = strings.chatEmptyHint,
            style = MaterialTheme.typography.bodySmall,
            color = NovaTheme.colors.textTertiary,
            textAlign = TextAlign.Center,
        )
    }
}
