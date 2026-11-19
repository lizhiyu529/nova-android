package com.nova.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nova.app.BuildConfig
import com.nova.app.data.AppLogs
import com.nova.app.ui.components.BackButton
import com.nova.app.ui.components.CenterToast
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.drawer.Conversation
import com.nova.app.ui.drawer.NovaDrawer
import com.nova.app.ui.drawer.pinnedAll
import com.nova.app.ui.drawer.removed
import com.nova.app.ui.drawer.renamed
import com.nova.app.ui.drawer.toggledPin
import com.nova.app.ui.theme.PaletteScheme
import com.nova.app.ui.home.HomeScreen
import com.nova.app.ui.search.SearchScreen
import com.nova.app.ui.settings.AppLanguage
import com.nova.app.ui.settings.AppThemeMode
import com.nova.app.ui.settings.DataManagementScreen
import com.nova.app.ui.settings.FontSizeScreen
import com.nova.app.ui.settings.PersonalizeScreen
import com.nova.app.ui.settings.SettingsScreen
import com.nova.app.ui.settings.settingsPageBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

private enum class Page { Search, Settings, Logs, FontSize, Personalize, DataManagement }

@Composable
fun NovaApp(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    fontScaleIndex: Int,
    followSystemFont: Boolean,
    onFontScaleChange: (Int) -> Unit,
    onFollowSystemFontChange: (Boolean) -> Unit,
    palette: PaletteScheme,
    onPaletteChange: (PaletteScheme) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var conversations by remember { mutableStateOf(sampleConversations(LocalDateTime.now())) }
    val referenceTime = remember { LocalDateTime.now() }
    var stack by rememberSaveable { mutableStateOf(listOf<Page>()) }
    var language by rememberSaveable { mutableStateOf(AppLanguage.Chinese) }
    var autoCollapseThinking by rememberSaveable { mutableStateOf(true) }
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(1_800)
            toast = null
        }
    }

    val current = stack.lastOrNull()

    fun push(page: Page) {
        stack = stack + page
        AppLogs.record(context, "open ${page.name.lowercase()}")
    }

    fun pop() {
        val leaving = stack.lastOrNull()
        stack = stack.dropLast(1)
        AppLogs.record(context, "close ${leaving?.name?.lowercase()}")
        if (stack.isEmpty()) {
            scope.launch { drawerState.open() }
        }
    }

    BackHandler(enabled = stack.isNotEmpty()) { pop() }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NovaDrawer(
                    conversations = conversations,
                    now = referenceTime,
                    accountEmail = "357******66@qq.com",
                    onOpenConversation = { scope.launch { drawerState.close() } },
                    onOpenSettings = {
                        scope.launch {
                            drawerState.close()
                            push(Page.Settings)
                        }
                    },
                    onOpenSearch = {
                        scope.launch {
                            drawerState.close()
                            push(Page.Search)
                        }
                    },
                    onRename = { id, title ->
                        conversations = conversations.renamed(id, title)
                        AppLogs.record(context, "rename conversation $id")
                    },
                    onTogglePin = { id ->
                        conversations = conversations.toggledPin(id)
                        AppLogs.record(context, "toggle pin $id")
                    },
                    onDelete = { ids ->
                        conversations = conversations.removed(ids)
                        AppLogs.record(context, "delete ${ids.size} conversation(s)")
                    },
                    onPinSelected = { ids ->
                        conversations = conversations.pinnedAll(ids)
                        AppLogs.record(context, "pin ${ids.size} conversation(s)")
                    },
                )
            },
        ) {
            HomeScreen(
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onNewChat = {
                    conversations = listOf(
                        Conversation(
                            id = UUID.randomUUID().toString(),
                            title = "新对话",
                            updatedAt = LocalDateTime.now(),
                        ),
                    ) + conversations
                    AppLogs.record(context, "new chat")
                },
            )
        }

        AnimatedVisibility(
            visible = current == Page.Search,
            enter = fadeIn(tween(320, delayMillis = 60)) +
                slideInHorizontally(tween(420, easing = FastOutSlowInEasing)) { it },
            exit = fadeOut(tween(260)) +
                slideOutHorizontally(tween(320, easing = FastOutSlowInEasing)) { it },
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface,
            ) {
                SearchScreen(
                    conversations = conversations,
                    now = referenceTime,
                    onBack = { pop() },
                    onOpenConversation = { conversation ->
                        AppLogs.record(context, "open conversation ${conversation.id}")
                        pop()
                    },
                )
            }
        }

        AnimatedVisibility(
            visible = stack.contains(Page.Settings),
            enter = fadeIn(tween(260, delayMillis = 40)) +
                slideInHorizontally(tween(460, easing = LinearOutSlowInEasing)) { it / 2 },
            exit = fadeOut(tween(200)) +
                slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it / 2 },
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = settingsPageBackground(),
            ) {
                SettingsPage(
                    language = language,
                    themeMode = themeMode,
                    onBack = { pop() },
                    onLanguageChange = { language = it },
                    onThemeChange = onThemeModeChange,
                    onOpenLogs = { push(Page.Logs) },
                    onFontSize = { push(Page.FontSize) },
                    onPersonalize = { push(Page.Personalize) },
                    onDataManagement = { push(Page.DataManagement) },
                    onCheckUpdate = {
                        AppLogs.record(context, "check update")
                        toast = "当前已是最新版本"
                    },
                )
            }
        }

        AnimatedVisibility(
            visible = current == Page.Logs,
            enter = fadeIn(tween(260, delayMillis = 40)) +
                slideInHorizontally(tween(460, easing = LinearOutSlowInEasing)) { it / 2 },
            exit = fadeOut(tween(200)) +
                slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it / 2 },
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = settingsPageBackground(),
            ) {
                LogPage(
                    onBack = { pop() },
                )
            }
        }

        AnimatedVisibility(
            visible = current == Page.FontSize,
            enter = fadeIn(tween(260, delayMillis = 40)) +
                slideInHorizontally(tween(460, easing = LinearOutSlowInEasing)) { it / 2 },
            exit = fadeOut(tween(200)) +
                slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it / 2 },
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = settingsPageBackground(),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SettingsTopBar(title = "字体大小", onBack = { pop() })
                    FontSizeScreen(
                        scaleIndex = fontScaleIndex,
                        followSystem = followSystemFont,
                        onScaleChange = onFontScaleChange,
                        onFollowSystemChange = onFollowSystemFontChange,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = current == Page.Personalize,
            enter = fadeIn(tween(260, delayMillis = 40)) +
                slideInHorizontally(tween(460, easing = LinearOutSlowInEasing)) { it / 2 },
            exit = fadeOut(tween(200)) +
                slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it / 2 },
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = settingsPageBackground(),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SettingsTopBar(title = "个性化", onBack = { pop() })
                    PersonalizeScreen(
                        autoCollapseThinking = autoCollapseThinking,
                        onAutoCollapseThinkingChange = { autoCollapseThinking = it },
                        palette = palette,
                        onPaletteChange = onPaletteChange,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = current == Page.DataManagement,
            enter = fadeIn(tween(260, delayMillis = 40)) +
                slideInHorizontally(tween(460, easing = LinearOutSlowInEasing)) { it / 2 },
            exit = fadeOut(tween(200)) +
                slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it / 2 },
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = settingsPageBackground(),
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SettingsTopBar(title = "数据管理", onBack = { pop() })
                    DataManagementScreen(
                        onExportAll = {
                            AppLogs.record(context, "export all conversations")
                            toast = "已导出 ${conversations.size} 条对话"
                        },
                        onDeleteAll = {
                            conversations = emptyList()
                            AppLogs.record(context, "delete all conversations")
                            toast = "已清空所有对话"
                        },
                    )
                }
            }
        }

        CenterToast(message = toast)
    }
}

@Composable
private fun SettingsPage(
    language: AppLanguage,
    themeMode: AppThemeMode,
    onBack: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    onOpenLogs: () -> Unit,
    onFontSize: () -> Unit,
    onPersonalize: () -> Unit,
    onDataManagement: () -> Unit,
    onCheckUpdate: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SettingsTopBar(onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding(),
        ) {
            SettingsScreen(
                language = language,
                themeMode = themeMode,
                versionName = BuildConfig.VERSION_NAME,
                onBack = onBack,
                onLanguageChange = onLanguageChange,
                onThemeChange = onThemeChange,
                onFontSize = onFontSize,
                onPersonalize = onPersonalize,
                onAccount = {},
                onDataManagement = onDataManagement,
                onCheckUpdate = onCheckUpdate,
                onOpenLogs = onOpenLogs,
            )
        }
    }
}

@Composable
private fun LogPage(onBack: () -> Unit) {
    val context = LocalContext.current
    var showCrashes by remember { mutableStateOf(false) }
    var version by remember { mutableStateOf(0) }
    val events = remember(version) { AppLogs.readEvents(context) }
    val crashes = remember(version) { AppLogs.readCrashes(context) }
    LaunchedEffect(Unit) {
        delay(120)
        version++
    }
    val content = if (showCrashes) crashes else events
    val path = if (showCrashes) {
        AppLogs.crashFile(context).absolutePath
    } else {
        AppLogs.eventFile(context).absolutePath
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
        ) {
            BackButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart),
            )
            Text(
                text = "运行日志",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center),
            )
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
                Surface(
                    onClick = {
                        if (showCrashes) {
                            AppLogs.clearCrashes(context)
                        } else {
                            AppLogs.clearEvents(context)
                        }
                        version++
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = NovaIcons.Trash,
                            contentDescription = "清空日志",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        PrimaryTabRow(
            selectedTabIndex = if (showCrashes) 1 else 0,
            modifier = Modifier.fillMaxWidth(),
            containerColor = settingsPageBackground(),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Tab(
                selected = !showCrashes,
                onClick = { showCrashes = false },
                text = { Text("运行记录") },
            )
            Tab(
                selected = showCrashes,
                onClick = { showCrashes = true },
                text = { Text("崩溃记录") },
            )
        }

        Text(
            text = path,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )

        if (content.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (showCrashes) "还没有崩溃记录" else "还没有运行记录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(14.dp),
                ) {
                    items(content) { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTopBar(title: String = "设置", onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        BackButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

private fun sampleConversations(now: LocalDateTime): List<Conversation> = listOf(
    Conversation("p1", "Kotlin 协程调度器原理", now.minusMinutes(4), pinned = true),
    Conversation("p2", "Compose 重组性能优化", now.minusMinutes(12), pinned = true),
    Conversation("p3", "Material 3 动态配色方案", now.minusHours(2)),
    Conversation("p4", "Gradle 构建速度排查", now.minusHours(6)),
    Conversation("p5", "Retrofit 拦截器写法", now.minusDays(1).minusHours(2)),
    Conversation("p6", "Room 数据库迁移", now.minusDays(1).minusHours(5)),
    Conversation("p7", "协程异常处理最佳实践", now.minusDays(3)),
    Conversation("p8", "Jetpack Navigation 参数传递", now.minusDays(5)),
    Conversation("p9", "单元测试 Mock 技巧", now.minusDays(9)),
)
