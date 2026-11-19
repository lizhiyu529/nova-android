package com.nova.app

import android.app.Application
import android.os.Build
import android.util.Log
import com.nova.app.data.CrashLogs
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NovaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        installCrashHandler()
    }

    private fun installCrashHandler() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { writeCrashLog(thread, throwable) }
                .onFailure { Log.e(TAG, "Failed to write crash log", it) }
            previous?.uncaughtException(thread, throwable)
        }
    }

    private fun writeCrashLog(thread: Thread, throwable: Throwable) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        val stackTrace = StringWriter().also { writer ->
            PrintWriter(writer).use { throwable.printStackTrace(it) }
        }.toString()

        val entry = buildString {
            appendLine("=== Nova Crash Report ===")
            appendLine("time: $timestamp")
            appendLine("thread: ${thread.name}")
            appendLine("app: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine("device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("exception: ${throwable.javaClass.name}: ${throwable.message}")
            appendLine(stackTrace)
            appendLine()
        }

        val target = CrashLogs.file(this)
        target.parentFile?.mkdirs()
        target.appendText(entry)
        Log.e(TAG, entry)
    }

    private companion object {
        const val TAG = "Nova"
    }
}
