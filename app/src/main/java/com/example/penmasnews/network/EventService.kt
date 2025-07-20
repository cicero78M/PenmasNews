package com.example.penmasnews.network

import com.example.penmasnews.BuildConfig
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.network.UserService
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
        val base = BuildConfig.API_BASE_URL.trimEnd('/')
        if (base.isBlank()) return emptyList()
        val url = base + "/api/events"
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
                    val rawUpdated = obj.optString("updated_by")
                    val updatedBy = if (rawUpdated.isNotBlank()) {
                        UserService.fetchUsername(token, rawUpdated) ?: rawUpdated
                    } else {
                        rawUpdated
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
                            obj.optString("tag"),
                            obj.optString("kategori"),
                            obj.optInt("event_id"),
                            obj.optString("created_at"),
                            lastUpdate,
                            obj.optString("username"),
                            updatedBy
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
        val base = BuildConfig.API_BASE_URL.trimEnd('/')
        if (base.isBlank()) return null
        val url = base + "/api/events"
        val obj = JSONObject()
        obj.put("event_date", event.date)
        obj.put("topic", event.topic)
        obj.put("assignee", event.assignee)
        obj.put("status", event.status)
        obj.put("content", event.content)
        obj.put("summary", event.summary)
        obj.put("image_path", event.imagePath)
        obj.put("tag", event.tag)
        obj.put("kategori", event.category)
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
                val updatedRaw = json.optString("updated_by")
                val updatedBy = if (updatedRaw.isNotBlank()) {
                    UserService.fetchUsername(token, updatedRaw) ?: updatedRaw
                } else {
                    updatedRaw
                }
                EditorialEvent(
                    json.optString("event_date"),
                    json.optString("topic"),
                    json.optString("assignee"),
                    json.optString("status"),
                    json.optString("content"),
                    json.optString("summary"),
                    json.optString("image_path"),
                    json.optString("tag"),
                    json.optString("kategori"),
                    json.optInt("event_id"),
                    json.optString("created_at"),
                    lastUpdate,
                    json.optString("username"),
                    updatedBy
                )
            }
        } catch (_: Exception) {
            null
        }
    }

    fun updateEvent(token: String, id: Int, event: EditorialEvent): Boolean {
        val base = BuildConfig.API_BASE_URL.trimEnd('/')
        if (base.isBlank()) return false
        val url = base + "/api/events/$id"
        val obj = JSONObject()
        obj.put("event_date", event.date)
        obj.put("topic", event.topic)
        obj.put("assignee", event.assignee)
        obj.put("status", event.status)
        obj.put("content", event.content)
        obj.put("summary", event.summary)
        obj.put("image_path", event.imagePath)
        obj.put("tag", event.tag)
        obj.put("kategori", event.category)
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
        val base = BuildConfig.API_BASE_URL.trimEnd('/')
        if (base.isBlank()) return false
        val url = base + "/api/events/$id"
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
