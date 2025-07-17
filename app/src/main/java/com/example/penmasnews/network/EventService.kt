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
                val body = resp.body?.string()
                if (!resp.isSuccessful || body == null) return emptyList()
                val array = JSONArray(body)
                val list = mutableListOf<EditorialEvent>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        EditorialEvent(
                            obj.optString("event_date"),
                            obj.optString("topic"),
                            obj.optString("assignee"),
                            obj.optString("status"),
                            obj.optString("content"),
                            obj.optString("summary"),
                            obj.optString("image_path")
                        )
                    )
                }
                list
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun createEvent(token: String, event: EditorialEvent): Boolean {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/events"
        val obj = JSONObject()
        obj.put("event_date", event.date)
        obj.put("topic", event.topic)
        obj.put("assignee", event.assignee)
        obj.put("status", event.status)
        obj.put("content", event.content)
        obj.put("summary", event.summary)
        obj.put("image_path", event.imagePath)
        val request = Request.Builder()
            .url(url)
            .post(obj.toString().toRequestBody(jsonType))
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }
}
