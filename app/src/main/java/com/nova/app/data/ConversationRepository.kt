package com.nova.app.data

import android.content.Context
import com.nova.app.data.chat.MessageJson
import com.nova.app.data.chat.MessagePart
import com.nova.app.data.chat.UIMessage
import com.nova.app.data.chat.Usage
import com.nova.app.data.db.ConversationEntity
import com.nova.app.data.db.MessageEntity
import com.nova.app.data.db.NovaDatabase
import com.nova.app.ui.drawer.Conversation
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class MessageRole(val value: String) {
    User("user"),
    Assistant("assistant"),
    System("system"),
    ;

    companion object {
        fun from(value: String): MessageRole =
            entries.firstOrNull { it.value == value } ?: User
    }
}

data class ChatMessage(
    val id: Long,
    val role: MessageRole,
    val content: String,
    val parts: List<MessagePart>,
    val usage: Usage?,
    val finished: Boolean,
    val createdAt: LocalDateTime,
)

class ConversationRepository(private val context: Context) {

    private val dao = NovaDatabase.get(context).dao()

    fun observeConversations(): Flow<List<Conversation>> =
        dao.observeConversations().map { list -> list.map { it.toModel() } }

    fun observeMessages(conversationId: String): Flow<List<ChatMessage>> =
        dao.observeMessages(conversationId).map { list -> list.map { it.toModel() } }

    suspend fun createConversation(title: String): String {
        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()
        dao.upsertConversation(
            ConversationEntity(
                id = id,
                title = title,
                updatedAt = now,
                createdAt = now,
            ),
        )
        return id
    }

    suspend fun rename(id: String, title: String) {
        dao.renameConversation(id, title, System.currentTimeMillis())
    }

    suspend fun setPinned(id: String, pinned: Boolean) {
        dao.setPinned(id, pinned)
    }

    suspend fun togglePinned(id: String, currentlyPinned: Boolean) {
        dao.setPinned(id, !currentlyPinned)
    }

    suspend fun pinAll(ids: Set<String>) {
        if (ids.isNotEmpty()) dao.pinAll(ids.toList())
    }

    suspend fun delete(ids: Set<String>) {
        if (ids.isNotEmpty()) dao.deleteConversations(ids.toList())
    }

    suspend fun clearAll() {
        dao.clearAllMessages()
        dao.clearConversations()
    }

    suspend fun appendMessage(conversationId: String, role: MessageRole, content: String): Long {
        val now = System.currentTimeMillis()
        val id = dao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = role.value,
                content = content,
                createdAt = now,
            ),
        )
        dao.touchConversation(conversationId, now)
        return id
    }

    suspend fun appendUIMessage(conversationId: String, message: UIMessage): Long {
        val now = System.currentTimeMillis()
        val id = dao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = message.role.value,
                content = message.toText(),
                partsJson = MessageJson.encode(message),
                usageJson = MessageJson.usageToJson(message.usage),
                finished = message.finished,
                steps = message.steps,
                elapsedMs = message.elapsedMs,
                createdAt = now,
            ),
        )
        dao.touchConversation(conversationId, now)
        return id
    }

    suspend fun updateConversationTitleFromFirstMessage(conversationId: String, content: String) {
        val messages = dao.messagesOf(conversationId)
        if (messages.count { it.role == MessageRole.User.value } > 1) return
        val title = content.trim().lineSequence().firstOrNull().orEmpty().take(28)
        if (title.isNotBlank()) dao.renameConversation(conversationId, title, System.currentTimeMillis())
    }

    suspend fun messagesOf(conversationId: String): List<ChatMessage> =
        dao.messagesOf(conversationId).map { it.toModel() }

    suspend fun uiMessagesOf(conversationId: String): List<UIMessage> =
        dao.messagesOf(conversationId).map { it.toUIMessage() }

    private fun ConversationEntity.toModel(): Conversation = Conversation(
        id = id,
        title = title,
        updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneId.systemDefault()),
        pinned = pinned,
    )

    private fun MessageEntity.toModel(): ChatMessage = ChatMessage(
        id = id,
        role = MessageRole.from(role),
        content = content,
        parts = MessageJson.decodeParts(partsJson),
        usage = MessageJson.decodeUsage(usageJson),
        finished = finished,
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault()),
    )

    private fun MessageEntity.toUIMessage(): UIMessage {
        val parsed = MessageJson.decodeParts(partsJson)
        val restored = if (parsed.isEmpty()) {
            if (content.isBlank()) emptyList() else listOf(MessagePart.Text(content))
        } else {
            parsed
        }
        return UIMessage(
            id = id.toString(),
            role = MessageJson.decodeRole(role),
            parts = restored,
            usage = MessageJson.decodeUsage(usageJson),
            finished = finished,
            steps = steps,
            elapsedMs = elapsedMs,
        )
    }
}
