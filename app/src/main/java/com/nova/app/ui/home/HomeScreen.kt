package com.nova.app.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.nova.app.data.AppLogs
import com.nova.app.data.CameraFiles
import com.nova.app.data.chat.ChatTurn
import com.nova.app.data.loadRecentPhotos
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.Strings
import com.nova.app.ui.components.AttachmentPanel
import com.nova.app.ui.components.ChatInputBar
import com.nova.app.ui.components.ChatMessageList
import com.nova.app.ui.components.EditorOverlay
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.components.PermissionDialog
import com.nova.app.ui.theme.NovaTheme
import com.nova.app.ui.theme.NovaTransitions

@Composable
fun HomeScreen(
    onOpenDrawer: () -> Unit,
    onNewChat: () -> Unit,
    turns: List<ChatTurn>,
    streamingText: String,
    loading: Boolean,
    errorText: String,
    onSendMessage: (String) -> Unit,
    onRetry: () -> Unit,
) {
    var draft by rememberSaveable { mutableStateOf("") }
    var showEditor by rememberSaveable { mutableStateOf(false) }
    var showAttachments by rememberSaveable { mutableStateOf(false) }
    var selectedPhotos by remember { mutableStateOf(setOf<String>()) }
    var recentPhotos by remember { mutableStateOf(listOf<String>()) }
    var showCameraPrompt by rememberSaveable { mutableStateOf(false) }
    var showMediaPrompt by rememberSaveable { mutableStateOf(false) }
    var mediaGranted by remember { mutableStateOf(false) }
    var photosLoading by remember { mutableStateOf(false) }
    val greeting = rememberGreeting()
    val context = LocalContext.current

    fun mediaPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    fun hasMediaPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            mediaPermission(),
        ) == PackageManager.PERMISSION_GRANTED
    }

    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        AppLogs.record(context, "media permission $granted")
        if (granted) {
            mediaGranted = true
        }
    }

    LaunchedEffect(showAttachments, mediaGranted) {
        if (showAttachments) {
            if (hasMediaPermission()) {
                photosLoading = true
                recentPhotos = loadRecentPhotos(context)
                photosLoading = false
            } else {
                showMediaPrompt = true
            }
        }
    }

    val cameraTarget = remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let {
            selectedPhotos = selectedPhotos + it.toString()
            AppLogs.record(context, "picked image ${it.lastPathSegment}")
        }
    }
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            AppLogs.record(context, "picked file ${it.lastPathSegment}")
            showAttachments = false
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
    ) { success ->
        val target = cameraTarget.value
        if (success && target != null) {
            selectedPhotos = selectedPhotos + target.toString()
            AppLogs.record(context, "captured photo")
        } else {
            AppLogs.record(context, "camera cancelled")
        }
        cameraTarget.value = null
    }

    fun launchCamera() {
        val (_, uri) = CameraFiles.createCaptureTarget(context)
        cameraTarget.value = uri
        cameraLauncher.launch(uri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            AppLogs.record(context, "camera permission denied")
        }
    }

    fun onCameraClick() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            launchCamera()
        } else {
            showCameraPrompt = true
        }
    }

    BackHandler(enabled = showAttachments && !showEditor) { showAttachments = false }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = NovaTheme.colors.pageBackground,
            topBar = {
                HomeTopBar(onOpenDrawer = onOpenDrawer, onNewChat = onNewChat)
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .navigationBarsPadding(),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        ChatInputBar(
                            value = draft,
                            onValueChange = { draft = it },
                            onSend = {
                            val text = draft.trim()
                            if (text.isNotEmpty()) {
                                onSendMessage(text)
                                draft = ""
                            }
                        },
                            onAttach = { showAttachments = !showAttachments },
                            onExpand = { showEditor = true },
                        )
                    }
                    AnimatedVisibility(
                        visible = showAttachments,
                        enter = NovaTransitions.panelEnter,
                        exit = NovaTransitions.panelExit,
                    ) {
                        AttachmentPanel(
                            recentPhotos = recentPhotos,
                            selectedPhotos = selectedPhotos,
                            loading = photosLoading,
                            onTogglePhoto = { uri ->
                                selectedPhotos = if (uri in selectedPhotos) {
                                    selectedPhotos - uri
                                } else {
                                    selectedPhotos + uri
                                }
                            },
                            onCamera = { onCameraClick() },
                            onGallery = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                )
                            },
                            onFile = {
                                fileLauncher.launch(arrayOf("*/*"))
                            },
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                        )
                    }
                }
            },
        ) { innerPadding ->
            if (turns.isEmpty() && streamingText.isEmpty() && !loading && errorText.isEmpty()) {
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
            } else {
                ChatMessageList(
                    turns = turns,
                    streamingText = streamingText,
                    loading = loading,
                    errorText = errorText,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }

        if (showEditor) {
            EditorOverlay(
                text = draft,
                onTextChange = { draft = it },
                onSend = {
                    draft = ""
                    showEditor = false
                },
                onDismiss = { showEditor = false },
            )
        }
    }

    if (showCameraPrompt) {
        PermissionDialog(
            icon = NovaIcons.Camera,
            title = NovaStrings.strings.permissionCameraTitle,
            message = NovaStrings.strings.permissionCameraMessage,
            onConfirm = {
                showCameraPrompt = false
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onDismiss = { showCameraPrompt = false },
        )
    }

    if (showMediaPrompt) {
        PermissionDialog(
            icon = NovaIcons.Gallery,
            title = NovaStrings.strings.permissionMediaTitle,
            message = NovaStrings.strings.permissionMediaMessage,
            onConfirm = {
                showMediaPrompt = false
                mediaPermissionLauncher.launch(mediaPermission())
            },
            onDismiss = {
                showMediaPrompt = false
                recentPhotos = emptyList()
            },
        )
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
                contentDescription = NovaStrings.strings.openMenu,
                tint = NovaTheme.colors.textPrimary,
                modifier = Modifier.size(24.dp),
            )
        }
        IconButton(onClick = onNewChat) {
            Icon(
                imageVector = NovaIcons.NewChat,
                contentDescription = NovaStrings.strings.newConversation,
                tint = NovaTheme.colors.textPrimary,
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
            color = NovaTheme.colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = greeting.hint,
            style = MaterialTheme.typography.bodyLarge,
            color = NovaTheme.colors.textSecondary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    NovaTheme(darkTheme = false, strings = Strings(LocalContext.current)) {
        HomeScreen(
            onOpenDrawer = {},
            onNewChat = {},
            turns = emptyList(),
            streamingText = "",
            loading = false,
            errorText = "",
            onSendMessage = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenDarkPreview() {
    NovaTheme(darkTheme = true, strings = Strings(LocalContext.current)) {
        HomeScreen(
            onOpenDrawer = {},
            onNewChat = {},
            turns = emptyList(),
            streamingText = "",
            loading = false,
            errorText = "",
            onSendMessage = {},
            onRetry = {},
        )
    }
}
