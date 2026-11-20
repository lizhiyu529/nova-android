package com.nova.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ConversationEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class NovaDatabase : RoomDatabase() {

    abstract fun dao(): NovaDao

    companion object {

        @Volatile
        private var instance: NovaDatabase? = null

        fun get(context: Context): NovaDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NovaDatabase::class.java,
                    "nova.db",
                ).build().also { instance = it }
            }
        }
    }
}
