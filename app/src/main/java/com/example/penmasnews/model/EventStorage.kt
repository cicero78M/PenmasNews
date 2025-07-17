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

    fun addEvent(context: Context, event: EditorialEvent): EditorialEvent? {
        val auth = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = auth.getString("token", null) ?: return null
        return EventService.createEvent(token, event)
    }

    fun updateEvent(context: Context, event: EditorialEvent): Boolean {
        val auth = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = auth.getString("token", null) ?: return false
        if (event.id == 0) return false
        return EventService.updateEvent(token, event.id, event)
    }

    fun deleteEvent(context: Context, id: Int): Boolean {
        val auth = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = auth.getString("token", null) ?: return false
        return EventService.deleteEvent(token, id)
    }

    fun saveEvents(context: Context, events: List<EditorialEvent>) {
        val auth = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = auth.getString("token", null) ?: return
        for (event in events) {
            if (event.id == 0) {
                EventService.createEvent(token, event)
            } else {
                EventService.updateEvent(token, event.id, event)
            }
        }
    }
}
