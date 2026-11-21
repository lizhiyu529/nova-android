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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nova.app.data.chat.ChatPart
import com.nova.app.data.chat.ChatTurn
import com.nova.app.data.chat.visibleParts
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.theme.NovaTheme

private fun mergeParts(parts: List<ChatPart>): List<ChatPart> {
    val visible = parts.visibleParts()
    if (visible.size <= 1) return visible
    val result = mutableListOf<ChatPart>()
    var reasoning = StringBuilder()
    var text = StringBuilder()

    fun flushReasoning() {
        if (reasoning.isNotEmpty()) {
            result.add(ChatPart.Reasoning(reasoning.toString(), finished = true))
            reasoning = StringBuilder()
        }
    }

    fun flushText() {
        if (text.isNotEmpty()) {
            result.add(ChatPart.Text(text.toString(), finished = true))
            text = StringBuilder()
        }
    }

    visible.forEach { part ->
        when (part) {
            is ChatPart.Reasoning -> {
                flushText()
                reasoning.append(part.text)
            }

            is ChatPart.Text -> {
                flushReasoning()
                text.append(part.text)
            }

            is ChatPart.Tool -> {
                flushReasoning()
                flushText()
                result.add(part)
            }
        }
    }
    flushReasoning()
    flushText()
    return result
}

@Composable
fun ChatMessageList(
    turns: List<ChatTurn>,
    liveParts: List<ChatPart>,
    loading: Boolean,
    errorText: String,
    reasoningExpanded: Boolean,
    onReasoningToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val merged = mergeParts(liveParts)
    val hasLive = loading || errorText.isNotEmpty() || merged.isNotEmpty()
    val total = turns.size + if (hasLive) 1 else 0

    LaunchedEffect(total) {
        if (total > 0) listState.animateScrollToItem(total - 1)
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(turns) { index, turn ->
            key("turn-$index-${turn.role}") {
                if (turn.role == "user") {
                    UserBubble(turn.content)
                } else if (turn.role == "assistant") {
                    AssistantParts(
                        parts = mergeParts(
                            turn.parts.ifEmpty { listOf(ChatPart.Text(turn.content)) },
                        ),
                        expanded = reasoningExpanded,
                        onToggle = onReasoningToggle,
                        streaming = false,
                    )
                }
            }
        }

        if (hasLive) {
            item(key = "live") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (merged.isNotEmpty()) {
                        AssistantParts(
                            parts = merged,
                            expanded = reasoningExpanded,
                            onToggle = onReasoningToggle,
                            streaming = true,
                        )
                    }
                    if (errorText.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        ErrorBlock(errorText)
                    }
                    if (loading && merged.isEmpty()) {
                        TypingDots(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }

        item(key = "tail") {
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun AssistantParts(
    parts: List<ChatPart>,
    expanded: Boolean,
    onToggle: () -> Unit,
    streaming: Boolean,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        parts.forEachIndexed { index, part ->
            key("part-$index-${part.javaClass.simpleName}") {
                when (part) {
                    is ChatPart.Reasoning -> {
                        if (part.text.isNotEmpty()) {
                            ReasoningPanel(
                                text = part.text,
                                expanded = expanded,
                                running = streaming && !part.finished,
                                onToggle = onToggle,
                            )
                        }
                    }

                    is ChatPart.Tool -> {
                        ToolRunPanel(part = part)
                    }

                    is ChatPart.Text -> {
                        if (part.text.isNotEmpty()) {
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
