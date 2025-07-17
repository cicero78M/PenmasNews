package com.example.penmasnews.model

import android.content.Context
import com.example.penmasnews.network.EventService

/** Utility object for persisting editorial events in SharedPreferences. */
object EventStorage {
    const val PREFS_NAME = "editorial_events"

    fun loadEvents(context: Context): MutableList<EditorialEvent> {
        val auth = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = auth.getString("token", null) ?: return mutableListOf()
        return EventService.fetchEvents(token).toMutableList()
    }

    fun saveEvents(context: Context, events: List<EditorialEvent>) {
        val auth = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = auth.getString("token", null) ?: return
        for (event in events) {
            EventService.createEvent(token, event)
        }
    }
}
