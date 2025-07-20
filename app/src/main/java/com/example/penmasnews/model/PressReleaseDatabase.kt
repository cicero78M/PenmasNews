package com.example.penmasnews.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** SQLite storage for press release specific fields */
object PressReleaseDatabase {
    private const val DB_NAME = "press_release_detail.db"
    private const val DB_VERSION = 1
    private const val TABLE_NAME = "press_release_detail"

    private class Helper(context: Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_NAME (
                    event_id INTEGER PRIMARY KEY,
                    judul TEXT,
                    dasar TEXT,
                    tersangka TEXT,
                    tkp TEXT,
                    kronologi TEXT,
                    modus TEXT,
                    barang_bukti TEXT,
                    pasal TEXT,
                    ancaman TEXT,
                    catatan TEXT
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

    fun save(context: Context, data: PressReleaseData) {
        val values = ContentValues().apply {
            put("event_id", data.eventId)
            put("judul", data.judul)
            put("dasar", data.dasar)
            put("tersangka", data.tersangka)
            put("tkp", data.tkp)
            put("kronologi", data.kronologi)
            put("modus", data.modus)
            put("barang_bukti", data.barangBukti)
            put("pasal", data.pasal)
            put("ancaman", data.ancaman)
            put("catatan", data.catatan)
        }
        getHelper(context).writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }
}
