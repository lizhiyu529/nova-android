package com.nova.app.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 相册里最近的照片，供附件面板直接勾选。 */
suspend fun loadRecentPhotos(context: Context, limit: Int = 30): List<String> {
    return withContext(Dispatchers.IO) {
        val result = mutableListOf<String>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_MODIFIED,
        )
        val sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC"
        runCatching {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder,
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext() && result.size < limit) {
                    val id = cursor.getLong(idColumn)
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id,
                    )
                    result += uri.toString()
                }
            }
        }
        result
    }
}
