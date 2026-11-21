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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nova.app.data.chat.ChatTurn
import com.nova.app.data.chat.ToolRun
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.theme.NovaTheme

@Composable
fun ChatMessageList(
    turns: List<ChatTurn>,
    streamingText: String,
    reasoningText: String,
    toolRuns: List<ToolRun>,
    loading: Boolean,
    errorText: String,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val hasLive = loading || streamingText.isNotEmpty() || errorText.isNotEmpty() ||
        reasoningText.isNotEmpty() || toolRuns.isNotEmpty()
    val itemCount = turns.size + if (hasLive) 1 else 0

    LaunchedEffect(itemCount, streamingText.length, reasoningText.length, toolRuns.size) {
        if (itemCount > 0) {
            listState.animateScrollToItem(itemCount - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        itemsIndexed(turns) { _, turn ->
            if (turn.role == "user") {
                UserBubble(turn.content)
            } else if (turn.role == "assistant") {
                AssistantBlock(turn.content)
            }
        }

        if (hasLive) {
            item(key = "live") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (reasoningText.isNotEmpty() && streamingText.isEmpty() && errorText.isEmpty()) {
                        ReasoningPanel(text = reasoningText)
                    }
                    toolRuns.forEach { run ->
                        ToolRunPanel(run = run)
                    }
                    when {
                        errorText.isNotEmpty() -> ErrorBlock(errorText)
                        streamingText.isNotEmpty() -> AssistantBlock(streamingText)
                        toolRuns.isNotEmpty() -> Unit
                        loading -> AssistantLoading()
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
private fun AssistantBlock(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        MarkdownView(text = text)
    }
}

@Composable
private fun AssistantLoading() {
    Box(modifier = Modifier.fillMaxWidth()) {
        TypingDots(modifier = Modifier.padding(vertical = 8.dp))
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
