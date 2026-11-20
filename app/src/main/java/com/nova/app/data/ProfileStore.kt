package com.nova.app.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.edit
import java.io.File

data class UserProfile(
    val nickname: String = "",
    val avatarPath: String = "",
)

object ProfileStore {

    private const val PREFS = "nova.profile"
    private const val KEY_NICKNAME = "nickname"
    private const val KEY_AVATAR = "avatar_path"
    private const val AVATAR_FILE = "avatar.png"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(context: Context): UserProfile {
        val p = prefs(context)
        return UserProfile(
            nickname = p.getString(KEY_NICKNAME, "") ?: "",
            avatarPath = p.getString(KEY_AVATAR, "") ?: "",
        )
    }

    fun setNickname(context: Context, nickname: String) {
        prefs(context).edit { putString(KEY_NICKNAME, nickname.trim()) }
    }

    fun saveAvatar(context: Context, bitmap: Bitmap): String {
        val file = File(context.applicationContext.filesDir, AVATAR_FILE)
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        prefs(context).edit { putString(KEY_AVATAR, file.absolutePath) }
        return file.absolutePath
    }

    fun clearAvatar(context: Context): String {
        val file = File(context.applicationContext.filesDir, AVATAR_FILE)
        if (file.exists()) file.delete()
        prefs(context).edit { remove(KEY_AVATAR) }
        return ""
    }

    fun avatarFile(context: Context): File =
        File(context.applicationContext.filesDir, AVATAR_FILE)
}
