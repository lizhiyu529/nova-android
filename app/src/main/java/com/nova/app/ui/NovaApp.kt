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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nova.app.BuildConfig
import com.nova.app.ui.components.NovaIcons
import com.nova.app.ui.drawer.Conversation
import com.nova.app.ui.drawer.NovaDrawer
import com.nova.app.ui.home.HomeScreen
import com.nova.app.ui.search.SearchScreen
import com.nova.app.ui.settings.AppLanguage
import com.nova.app.ui.settings.AppThemeMode
import com.nova.app.ui.settings.SettingsScreen
import com.nova.app.ui.settings.settingsPageBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

private enum class Overlay { None, Search, Settings }

@Composable
fun NovaApp(
    themeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000)
            now = LocalDateTime.now()
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var conversations by remember { mutableStateOf(sampleConversations(now)) }
    var overlay by remember { mutableStateOf(Overlay.None) }
    var language by remember { mutableStateOf(AppLanguage.Chinese) }

    fun closeOverlay() {
        overlay = Overlay.None
        scope.launch { drawerState.open() }
    }

    BackHandler(enabled = overlay != Overlay.None) { closeOverlay() }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NovaDrawer(
                    conversations = conversations,
                    now = now,
                    accountEmail = "357******66@qq.com",
                    onOpenConversation = { scope.launch { drawerState.close() } },
                    onOpenSettings = {
                        scope.launch {
                            drawerState.close()
                            overlay = Overlay.Settings
                        }
                    },
                    onOpenSearch = {
                        scope.launch {
                            drawerState.close()
                            overlay = Overlay.Search
                        }
                    },
                )
            },
        ) {
            HomeScreen(
                now = now,
                onOpenDrawer = { scope.launch { drawerState.open() } },
                onNewChat = {
                    conversations = listOf(
                        Conversation(
                            id = UUID.randomUUID().toString(),
                            title = "新对话",
                            updatedAt = LocalDateTime.now(),
                        ),
                    ) + conversations
                },
            )
        }

        AnimatedVisibility(
            visible = overlay == Overlay.Search,
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
                    now = now,
                    onBack = { closeOverlay() },
                    onOpenConversation = { closeOverlay() },
                )
            }
        }

        AnimatedVisibility(
            visible = overlay == Overlay.Settings,
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
                    onBack = { closeOverlay() },
                    onLanguageChange = { language = it },
                    onThemeChange = onThemeModeChange,
                )
            }
        }
    }
}

@Composable
private fun SettingsPage(
    language: AppLanguage,
    themeMode: AppThemeMode,
    onBack: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onThemeChange: (AppThemeMode) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SettingsTopBar(onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                onFontSize = {},
                onPersonalize = {},
                onAccount = {},
                onDataManagement = {},
                onCheckUpdate = {},
            )
        }
    }
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Surface(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = NovaIcons.Back,
                        contentDescription = "返回",
                        modifier = Modifier.size(17.dp),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        Text(
            text = "设置",
            style = MaterialTheme.typography.titleLarge,
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
