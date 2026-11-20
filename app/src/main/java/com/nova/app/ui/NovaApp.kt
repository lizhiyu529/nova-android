package com.nova.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import com.nova.app.ui.NovaStrings
import com.nova.app.ui.Strings
import com.nova.app.ui.components.BackButton
import com.nova.app.ui.components.CenterToast
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.drawer.Conversation
import com.nova.app.ui.drawer.NovaDrawer
import com.nova.app.ui.drawer.pinnedAll
import com.nova.app.ui.drawer.removed
import com.nova.app.ui.drawer.renamed
import com.nova.app.ui.drawer.toggledPin
import com.nova.app.ui.home.HomeScreen
import com.nova.app.ui.search.SearchScreen
import com.nova.app.ui.settings.AppLanguage
import com.nova.app.ui.settings.AppThemeMode
import com.nova.app.ui.settings.DataManagementScreen
import com.nova.app.ui.settings.FontSizeScreen
import com.nova.app.ui.settings.PersonalizeScreen
import com.nova.app.ui.settings.SettingsScreen
import com.nova.app.ui.theme.NovaTheme
import com.nova.app.ui.theme.NovaTransitions
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class Page { Search, Settings, Logs, FontSize, Personalize, DataManagement }

@Composable
fun NovaApp(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLanguageNeedsRestart: () -> Unit,
    fontScaleIndex: Int,
    followSystemFont: Boolean,
    onFontScaleChange: (Int) -> Unit,
    onFollowSystemFontChange: (Boolean) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val strings = NovaStrings.strings
    var conversations by remember { mutableStateOf(sampleConversations(LocalDateTime.now(), strings)) }
    val referenceTime = remember { LocalDateTime.now() }
    var stack by rememberSaveable { mutableStateOf(listOf<Page>()) }
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
                        push(Page.Settings)
                    },
                    onOpenSearch = {
                        push(Page.Search)
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
            enter = NovaTransitions.rootEnter,
            exit = NovaTransitions.rootExit,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = NovaTheme.colors.containerLow,
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
            enter = NovaTransitions.pageEnter,
            exit = NovaTransitions.pageExit,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = NovaTheme.colors.pageBackground,
            ) {
                SettingsPage(
                    language = language,
                    themeMode = themeMode,
                    onBack = { pop() },
                    onLanguageChange = onLanguageChange,
                    onLanguageNeedsRestart = onLanguageNeedsRestart,
                    onThemeChange = onThemeModeChange,
                    onOpenLogs = { push(Page.Logs) },
                    onFontSize = { push(Page.FontSize) },
                    onPersonalize = { push(Page.Personalize) },
                    onDataManagement = { push(Page.DataManagement) },
                    onCheckUpdate = {
                        AppLogs.record(context, "check update")
                        toast = strings.upToDate
                    },
                )
            }
        }

        AnimatedVisibility(
            visible = current == Page.Logs,
            enter = NovaTransitions.pageEnter,
            exit = NovaTransitions.pageExit,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = NovaTheme.colors.pageBackground,
            ) {
                LogPage(
                    onBack = { pop() },
                )
            }
        }

        AnimatedVisibility(
            visible = current == Page.FontSize,
            enter = NovaTransitions.pageEnter,
            exit = NovaTransitions.pageExit,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = NovaTheme.colors.pageBackground,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SettingsTopBar(title = NovaStrings.strings.fontSize, onBack = { pop() })
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
            enter = NovaTransitions.pageEnter,
            exit = NovaTransitions.pageExit,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = NovaTheme.colors.pageBackground,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SettingsTopBar(title = NovaStrings.strings.personalization, onBack = { pop() })
                    PersonalizeScreen(
                        autoCollapseThinking = autoCollapseThinking,
                        onAutoCollapseThinkingChange = { autoCollapseThinking = it },
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = current == Page.DataManagement,
            enter = NovaTransitions.pageEnter,
            exit = NovaTransitions.pageExit,
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = NovaTheme.colors.pageBackground,
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SettingsTopBar(title = NovaStrings.strings.dataManagement, onBack = { pop() })
                    DataManagementScreen(
                        onExportAll = {
                            AppLogs.record(context, "export all conversations")
                            toast = strings.exported(conversations.size)
                        },
                        onDeleteAll = {
                            conversations = emptyList()
                            AppLogs.record(context, "delete all conversations")
                            toast = strings.cleared
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
    onLanguageNeedsRestart: () -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
    onOpenLogs: () -> Unit,
    onFontSize: () -> Unit,
    onPersonalize: () -> Unit,
    onDataManagement: () -> Unit,
    onCheckUpdate: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SettingsTopBar(title = NovaStrings.strings.settingsTitle, onBack = onBack)
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
                onLanguageNeedsRestart = onLanguageNeedsRestart,
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
                text = NovaStrings.strings.logsTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = NovaTheme.colors.textPrimary,
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
                    color = NovaTheme.colors.containerHighest,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = NovaIcons.Trash,
                            contentDescription = NovaStrings.strings.logsClear,
                            modifier = Modifier.size(16.dp),
                            tint = NovaTheme.colors.textSecondary,
                        )
                    }
                }
            }
        }

        PrimaryTabRow(
            selectedTabIndex = if (showCrashes) 1 else 0,
            modifier = Modifier.fillMaxWidth(),
            containerColor = NovaTheme.colors.pageBackground,
            contentColor = NovaTheme.colors.textPrimary,
        ) {
            Tab(
                selected = !showCrashes,
                onClick = { showCrashes = false },
                text = { Text(NovaStrings.strings.logsTabEvents) },
            )
            Tab(
                selected = showCrashes,
                onClick = { showCrashes = true },
                text = { Text(NovaStrings.strings.logsTabCrashes) },
            )
        }

        Text(
            text = path,
            style = MaterialTheme.typography.labelSmall,
            color = NovaTheme.colors.textSecondary,
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
                    text = if (showCrashes) NovaStrings.strings.logsEmptyCrashes else NovaStrings.strings.logsEmptyEvents,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NovaTheme.colors.textSecondary,
                )
            }
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                shape = MaterialTheme.shapes.medium,
                color = NovaTheme.colors.containerLow,
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
                            color = NovaTheme.colors.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTopBar(title: String, onBack: () -> Unit) {
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
            color = NovaTheme.colors.textPrimary,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

private fun sampleConversations(now: LocalDateTime, strings: Strings): List<Conversation> = listOf(
    Conversation("p1", strings.sampleConversation(1), now.minusMinutes(4), pinned = true),
    Conversation("p2", strings.sampleConversation(2), now.minusMinutes(12), pinned = true),
    Conversation("p3", strings.sampleConversation(3), now.minusHours(2)),
    Conversation("p4", strings.sampleConversation(4), now.minusHours(6)),
    Conversation("p5", strings.sampleConversation(5), now.minusDays(1).minusHours(2)),
    Conversation("p6", strings.sampleConversation(6), now.minusDays(1).minusHours(5)),
    Conversation("p7", strings.sampleConversation(7), now.minusDays(3)),
    Conversation("p8", strings.sampleConversation(8), now.minusDays(5)),
    Conversation("p9", strings.sampleConversation(9), now.minusDays(9)),
)
