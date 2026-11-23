package com.nova.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    val pinned: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = 0L,
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "conversation_id", index = true) val conversationId: String,
    val role: String,
    val content: String,
    @ColumnInfo(name = "parts_json") val partsJson: String = "",
    @ColumnInfo(name = "usage_json") val usageJson: String = "",
    val finished: Boolean = true,
    val steps: Int = 0,
    @ColumnInfo(name = "elapsed_ms") val elapsedMs: Long = 0L,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
