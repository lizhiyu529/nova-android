package com.nova.app.ui.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nova.app.data.AppLogs
import com.nova.app.ui.components.AttachmentPanel
import com.nova.app.ui.components.ChatInputBar
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.theme.NovaTheme

@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    onNewChat: () -> Unit,
) {
    var draft by rememberSaveable { mutableStateOf("") }
    var showAttachments by rememberSaveable { mutableStateOf(false) }
    val greeting = rememberGreeting()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { AppLogs.record(context, "picked image ${it.lastPathSegment}") }
        showAttachments = false
    }
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let { AppLogs.record(context, "picked file ${it.lastPathSegment}") }
        showAttachments = false
    }

    BackHandler(enabled = showAttachments) { showAttachments = false }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            HomeTopBar(onOpenDrawer = onOpenDrawer, onNewChat = onNewChat)
        },
        bottomBar = {
            Column {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                ) {
                    ChatInputBar(
                        value = draft,
                        onValueChange = { draft = it },
                        onSend = { draft = "" },
                        onAttach = { showAttachments = !showAttachments },
                    )
                }
                AnimatedVisibility(
                    visible = showAttachments,
                    enter = fadeIn(tween(200)) +
                        slideInVertically(tween(280)) { it / 2 },
                    exit = fadeOut(tween(160)) +
                        slideOutVertically(tween(200)) { it / 2 },
                ) {
                    AttachmentPanel(
                        onCamera = {
                            AppLogs.record(context, "camera picker")
                            showAttachments = false
                        },
                        onGallery = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                        onFile = {
                            fileLauncher.launch(arrayOf("*/*"))
                        },
                        modifier = Modifier.padding(bottom = 12.dp),
                    )
                }
                Spacer(Modifier.navigationBarsPadding().size(0.dp))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            GreetingBlock(
                greeting = greeting,
                modifier = Modifier.padding(bottom = 120.dp),
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
    greeting: Greeting,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = greeting.title,
            style = MaterialTheme.typography.headlineLargeEmphasized,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = greeting.hint,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    NovaTheme(darkTheme = false) {
        HomeScreen(
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
            onOpenDrawer = {},
            onNewChat = {},
        )
    }
}
