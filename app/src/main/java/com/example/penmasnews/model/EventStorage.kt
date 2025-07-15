package com.example.penmasnews.model

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/** Utility object for persisting editorial events in SharedPreferences. */
object EventStorage {
    const val PREFS_NAME = "editorial_events"

    fun loadEvents(prefs: SharedPreferences): MutableList<EditorialEvent> {
        val json = prefs.getString("events", "[]") ?: "[]"
        val array = JSONArray(json)
        val list = mutableListOf<EditorialEvent>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                EditorialEvent(
                    obj.optString("date"),
                    obj.optString("topic"),
                    obj.optString("assignee"),
                    obj.optString("status"),
                    obj.optString("content"),
                    obj.optString("summary")
                )
            )
        }
        return list
    }

    fun saveEvents(prefs: SharedPreferences, events: List<EditorialEvent>) {
        val array = JSONArray()
        for (item in events) {
            val obj = JSONObject()
            obj.put("date", item.date)
            obj.put("topic", item.topic)
            obj.put("assignee", item.assignee)
            obj.put("status", item.status)
            obj.put("content", item.content)
            obj.put("summary", item.summary)
            array.put(obj)
        }
        prefs.edit().putString("events", array.toString()).apply()
    }
}
