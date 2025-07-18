package com.example.penmasnews.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** SQLite-backed storage for change log entries */
object ChangeLogDatabase {
    private const val DB_NAME = "change_logs.db"
    private const val DB_VERSION = 1
    private const val TABLE_NAME = "logs"

    private class Helper(context: Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_NAME (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user TEXT,
                    status TEXT,
                    changes TEXT,
                    timestamp INTEGER
                )
                """.trimIndent()
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    @Volatile
    private var helper: Helper? = null

    private fun getHelper(context: Context): Helper {
        return helper ?: synchronized(this) {
            helper ?: Helper(context.applicationContext).also { helper = it }
        }
    }

    fun addLog(context: Context, entry: ChangeLogEntry) {
        val values = ContentValues().apply {
            put("user", entry.user)
            put("status", entry.status)
            put("changes", entry.changes)
            put("timestamp", entry.timestamp)
        }
        getHelper(context).writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun getLogs(context: Context): MutableList<ChangeLogEntry> {
        val list = mutableListOf<ChangeLogEntry>()
        val db = getHelper(context).readableDatabase
        val cursor = db.query(TABLE_NAME,
            arrayOf("user", "status", "changes", "timestamp"),
            null, null, null, null, "timestamp DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    ChangeLogEntry(
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getLong(3)
                    )
                )
            }
        }
        return list
    }
}
