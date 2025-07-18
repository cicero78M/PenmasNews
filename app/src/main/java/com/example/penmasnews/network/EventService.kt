package com.example.penmasnews.network

import com.example.penmasnews.BuildConfig
import com.example.penmasnews.model.EditorialEvent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object EventService {
    private val client = OkHttpClient()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    fun fetchEvents(token: String): List<EditorialEvent> {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/events"
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string() ?: return emptyList()
                if (!resp.isSuccessful) return emptyList()
                val root = JSONObject(body)
                val array = root.optJSONArray("data") ?: JSONArray()
                val list = mutableListOf<EditorialEvent>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val lastUpdate = if (obj.has("last_update")) {
                        obj.optString("last_update")
                    } else {
                        obj.optString("last_updated")
                    }
                    list.add(
                        EditorialEvent(
                            obj.optString("event_date"),
                            obj.optString("topic"),
                            obj.optString("assignee"),
                            obj.optString("status"),
                            obj.optString("content"),
                            obj.optString("summary"),
                            obj.optString("image_path"),
                            obj.optInt("event_id"),
                            obj.optString("created_at"),
                            lastUpdate,
                            obj.optString("username"),
                            obj.optString("updated_by")
                        )
                    )
                }
                list
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun createEvent(token: String, event: EditorialEvent): EditorialEvent? {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/events"
        val obj = JSONObject()
        obj.put("event_date", event.date)
        obj.put("topic", event.topic)
        obj.put("assignee", event.assignee)
        obj.put("status", event.status)
        obj.put("content", event.content)
        obj.put("summary", event.summary)
        obj.put("image_path", event.imagePath)
        obj.put("created_at", event.createdAt)
        obj.put("last_update", event.lastUpdate)
        val request = Request.Builder()
            .url(url)
            .post(obj.toString().toRequestBody(jsonType))
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string() ?: return null
                if (!resp.isSuccessful) return null
                val json = JSONObject(body).optJSONObject("data") ?: return null
                val lastUpdate = if (json.has("last_update")) {
                    json.optString("last_update")
                } else {
                    json.optString("last_updated")
                }
                EditorialEvent(
                    json.optString("event_date"),
                    json.optString("topic"),
                    json.optString("assignee"),
                    json.optString("status"),
                    json.optString("content"),
                    json.optString("summary"),
                    json.optString("image_path"),
                    json.optInt("event_id"),
                    json.optString("created_at"),
                    lastUpdate,
                    json.optString("username"),
                    json.optString("updated_by")
                )
            }
        } catch (_: Exception) {
            null
        }
    }

    fun updateEvent(token: String, id: Int, event: EditorialEvent): Boolean {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/events/$id"
        val obj = JSONObject()
        obj.put("event_date", event.date)
        obj.put("topic", event.topic)
        obj.put("assignee", event.assignee)
        obj.put("status", event.status)
        obj.put("content", event.content)
        obj.put("summary", event.summary)
        obj.put("image_path", event.imagePath)
        obj.put("last_update", event.lastUpdate)
        obj.put("updated_by", event.updatedBy)
        val request = Request.Builder()
            .url(url)
            .put(obj.toString().toRequestBody(jsonType))
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }

    fun deleteEvent(token: String, id: Int): Boolean {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/events/$id"
        val request = Request.Builder()
            .url(url)
            .delete()
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }
}
