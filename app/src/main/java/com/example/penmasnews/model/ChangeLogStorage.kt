package com.example.penmasnews.model

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/** Utility object for persisting change log entries */
object ChangeLogStorage {
    const val PREFS_NAME = "change_logs"

    fun loadLogs(prefs: SharedPreferences): MutableList<ChangeLogEntry> {
        val json = prefs.getString("logs", "[]") ?: "[]"
        val array = JSONArray(json)
        val list = mutableListOf<ChangeLogEntry>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                ChangeLogEntry(
                    obj.optString("user"),
                    obj.optString("status"),
                    obj.optString("changes"),
                    obj.optLong("timestamp")
                )
            )
        }
        return list
    }

    fun saveLogs(prefs: SharedPreferences, logs: List<ChangeLogEntry>) {
        val array = JSONArray()
        for (log in logs) {
            val obj = JSONObject()
            obj.put("user", log.user)
            obj.put("status", log.status)
            obj.put("changes", log.changes)
            obj.put("timestamp", log.timestamp)
            array.put(obj)
        }
        prefs.edit().putString("logs", array.toString()).apply()
    }
}
