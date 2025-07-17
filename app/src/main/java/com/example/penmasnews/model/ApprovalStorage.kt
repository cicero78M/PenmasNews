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
                    obj.optString("summary"),
                    obj.optString("imagePath"),
                    obj.optInt("id"),
                    obj.optString("createdAt"),
                    obj.optString("updatedAt"),
                    obj.optString("username")
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
            obj.put("imagePath", item.imagePath)
            obj.put("id", item.id)
            obj.put("createdAt", item.createdAt)
            obj.put("updatedAt", item.updatedAt)
            obj.put("username", item.username)
            array.put(obj)
        }
        prefs.edit().putString("events", array.toString()).apply()
    }
}
