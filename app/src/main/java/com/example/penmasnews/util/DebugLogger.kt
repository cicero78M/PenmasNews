package com.example.penmasnews.util

import android.content.Context
import android.util.Log
import com.example.penmasnews.BuildConfig
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Simple logger that saves debug messages to a file when DEBUG is enabled. */
object DebugLogger {
    private const val LOG_TAG = "PenmasNews"
    private const val LOG_FILE = "debug.log"

    fun log(context: Context, message: String) {
        if (!BuildConfig.DEBUG) {
            Log.d(LOG_TAG, message)
            return
        }
        Log.d(LOG_TAG, message)
        try {
            val file = File(context.filesDir, LOG_FILE)
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
            file.appendText("[$timestamp] $message\n")
        } catch (_: IOException) {
            // ignore write errors
        }
    }

    fun readLog(context: Context): String {
        val file = File(context.filesDir, LOG_FILE)
        return if (file.exists()) file.readText() else ""
    }
}
