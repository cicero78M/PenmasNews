package com.example.penmasnews.model

import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/** Utility object for persisting approval requests */
object ApprovalStorage {
    const val PREFS_NAME = "approval_requests"

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
