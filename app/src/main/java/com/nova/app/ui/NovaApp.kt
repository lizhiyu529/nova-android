package com.nova.app.ui

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.nova.app.ui.drawer.Conversation
import com.nova.app.ui.drawer.NovaDrawer
import com.nova.app.ui.home.HomeScreen
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NovaDrawer(
                conversations = conversations,
                now = now,
                accountEmail = "357******66@qq.com",
                onNewChat = {
                    conversations = listOf(
                        Conversation(
                            id = UUID.randomUUID().toString(),
                            title = "新对话",
                            updatedAt = LocalDateTime.now(),
                        ),
                    ) + conversations
                    scope.launch { drawerState.close() }
                },
                onOpenConversation = { scope.launch { drawerState.close() } },
                onOpenSettings = { scope.launch { drawerState.close() } },
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
