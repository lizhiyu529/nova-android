package com.nova.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nova.app.ui.components.ChatInputBar
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.theme.NovaTheme
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    now: LocalDateTime,
    onOpenDrawer: () -> Unit,
    onNewChat: () -> Unit,
) {
    var draft by rememberSaveable { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            HomeTopBar(onOpenDrawer = onOpenDrawer, onNewChat = onNewChat)
        },
        bottomBar = {
            ChatInputBar(
                value = draft,
                onValueChange = { draft = it },
                onSend = { draft = "" },
                onAttach = {},
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp)
                    .navigationBarsPadding()
                    .imePadding(),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            GreetingBlock(
                greeting = greetingFor(now.hour),
                modifier = Modifier.padding(horizontal = 32.dp),
            )
        }
    }
}

@Composable
private fun HomeTopBar(onOpenDrawer: () -> Unit, onNewChat: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onOpenDrawer) {
            Icon(
                imageVector = NovaIcons.Menu,
                contentDescription = "打开菜单",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp),
            )
        }
        IconButton(onClick = onNewChat) {
            Icon(
                imageVector = NovaIcons.NewChat,
                contentDescription = "新建对话",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun GreetingBlock(
    greeting: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = greeting,
        style = MaterialTheme.typography.headlineLargeEmphasized,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    NovaTheme(darkTheme = false) {
        HomeScreen(
            now = LocalDateTime.of(2026, 11, 19, 14, 32),
            onOpenDrawer = {},
            onNewChat = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenDarkPreview() {
    NovaTheme(darkTheme = true) {
        HomeScreen(
            now = LocalDateTime.of(2026, 11, 19, 23, 48),
            onOpenDrawer = {},
            onNewChat = {},
        )
    }
}
