package com.nova.app.ui.drawer

import java.time.Duration
import java.time.LocalDateTime

enum class ConversationGroup(val label: String) {
    Pinned("置顶"),
    Today("今天"),
    Yesterday("昨天"),
    LastWeek("7 天内"),
    Earlier("更早"),
}

data class Conversation(
    val id: String,
    val title: String,
    val updatedAt: LocalDateTime,
    val pinned: Boolean = false,
)

fun groupOf(conversation: Conversation, now: LocalDateTime): ConversationGroup {
    if (conversation.pinned) return ConversationGroup.Pinned
    val days = Duration.between(conversation.updatedAt, now).toDays()
    return when {
        days < 1 -> ConversationGroup.Today
        days < 2 -> ConversationGroup.Yesterday
        days < 7 -> ConversationGroup.LastWeek
        else -> ConversationGroup.Earlier
    }
}

fun groupConversations(
    conversations: List<Conversation>,
    now: LocalDateTime,
): List<Pair<ConversationGroup, List<Conversation>>> {
    return ConversationGroup.entries
        .mapNotNull { group ->
            val items = conversations
                .filter { groupOf(it, now) == group }
                .sortedByDescending { it.updatedAt }
            if (items.isEmpty()) null else group to items
        }
}
