package com.nova.app.ui.drawer

import com.nova.app.ui.Strings
import java.time.Duration
import java.time.LocalDateTime

enum class ConversationGroup {
    Pinned,
    Today,
    Yesterday,
    LastWeek,
    Earlier,
    ;

    fun label(strings: Strings): String = when (this) {
        Pinned -> strings.groupPinned
        Today -> strings.groupToday
        Yesterday -> strings.groupYesterday
        LastWeek -> strings.groupLastWeek
        Earlier -> strings.groupEarlier
    }
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

fun List<Conversation>.renamed(id: String, newTitle: String): List<Conversation> {
    return map { if (it.id == id) it.copy(title = newTitle, updatedAt = LocalDateTime.now()) else it }
}

fun List<Conversation>.toggledPin(id: String): List<Conversation> {
    return map { if (it.id == id) it.copy(pinned = !it.pinned) else it }
}

fun List<Conversation>.pinnedAll(ids: Set<String>): List<Conversation> {
    return map { if (it.id in ids) it.copy(pinned = true) else it }
}

fun List<Conversation>.removed(ids: Set<String>): List<Conversation> {
    return filterNot { it.id in ids }
}
