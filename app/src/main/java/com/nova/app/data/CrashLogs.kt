package com.nova.app.data

import android.content.Context
import java.io.File

object CrashLogs {

    private const val DIR = "logs"
    private const val FILE = "crash.log"

    fun file(context: Context): File = File(File(context.filesDir, DIR), FILE)

    fun read(context: Context): String {
        val target = file(context)
        if (!target.exists()) return ""
        return runCatching { target.readText() }.getOrDefault("")
    }

    fun clear(context: Context) {
        runCatching { file(context).delete() }
    }
}
