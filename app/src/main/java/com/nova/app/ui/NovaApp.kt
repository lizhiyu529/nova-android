package com.nova.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nova.app.ui.drawer.Conversation
import com.nova.app.ui.drawer.NovaDrawer
import com.nova.app.ui.home.HomeScreen
import com.nova.app.ui.search.SearchScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun NovaApp() {
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
    var searching by remember { mutableStateOf(false) }

    BackHandler(enabled = searching) {
        searching = false
        scope.launch { drawerState.open() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                NovaDrawer(
                    conversations = conversations,
                    now = now,
                    accountEmail = "357******66@qq.com",
                    onOpenConversation = { scope.launch { drawerState.close() } },
                    onOpenSettings = { scope.launch { drawerState.close() } },
                    onOpenSearch = {
                        scope.launch {
                            drawerState.close()
                            searching = true
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
            visible = searching,
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
                    onBack = {
                        searching = false
                        scope.launch { drawerState.open() }
                    },
                    onOpenConversation = {
                        searching = false
                    },
                )
            }
        }
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
