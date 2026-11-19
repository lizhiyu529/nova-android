package com.nova.app.data

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

object AppLogs {

    private const val TAG = "Nova"
    private const val DIR = "logs"
    private const val CRASH_FILE = "crash.log"
    private const val EVENT_FILE = "app.log"
    private const val MAX_BYTES = 256 * 1024L
    private const val KEEP_LINES = 400
    private const val DISPLAY_LINES = 500

    /** 单线程串行写，避免主线程磁盘 IO 与并发写乱序。 */
    private val writer = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "nova-log-writer").apply { isDaemon = true }
    }

    fun crashFile(context: Context): File = File(dir(context), CRASH_FILE)

    fun eventFile(context: Context): File = File(dir(context), EVENT_FILE)

    private fun dir(context: Context): File = File(context.filesDir, DIR).apply { mkdirs() }

    fun record(context: Context, event: String) {
        val app = context.applicationContext
        val line = "${stamp()} [$event]"
        Log.d(TAG, event)
        writer.execute {
            runCatching {
                val file = eventFile(app)
                file.appendText("$line\n")
                trim(file)
            }.onFailure { Log.w(TAG, "write event log failed", it) }
        }
    }

    fun record(context: Context, event: String, throwable: Throwable) {
        val app = context.applicationContext
        Log.d(TAG, event, throwable)
        writer.execute {
            runCatching {
                val file = eventFile(app)
                file.appendText(
                    "${stamp()} [$event] ${throwable.javaClass.simpleName}: ${throwable.message}\n",
                )
                trim(file)
            }.onFailure { Log.w(TAG, "write event log failed", it) }
        }
    }

    /** 只取末尾若干行，避免把整份日志读进内存再渲染。 */
    fun readEvents(context: Context): List<String> = readTail(eventFile(context))

    fun readCrashes(context: Context): List<String> = readTail(crashFile(context))

    private fun readTail(file: File): List<String> = runCatching {
        if (!file.exists()) return emptyList()
        file.useLines { lines -> lines.takeLast(DISPLAY_LINES).toList() }
    }.getOrDefault(emptyList())

    fun clearEvents(context: Context) {
        val app = context.applicationContext
        writer.execute { runCatching { eventFile(app).delete() } }
    }

    fun clearCrashes(context: Context) {
        val app = context.applicationContext
        writer.execute { runCatching { crashFile(app).delete() } }
    }

    private fun trim(file: File) {
        if (file.length() <= MAX_BYTES) return
        val keep = file.useLines { it.takeLast(KEEP_LINES).toList() }
        file.writeText(keep.joinToString("\n", postfix = "\n"))
    }

    private fun stamp(): String =
        SimpleDateFormat("MM-dd HH:mm:ss", Locale.US).format(Date())
}
