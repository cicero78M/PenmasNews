package com.example.penmasnews.network

import com.example.penmasnews.BuildConfig
import com.example.penmasnews.model.ChangeLogEntry
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant

object LogService {
    private val client = OkHttpClient()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    fun fetchLogs(token: String, eventId: Int): List<ChangeLogEntry> {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/events/$eventId/logs"
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string() ?: return emptyList()
                if (!resp.isSuccessful) return emptyList()
                val array = JSONObject(body).optJSONArray("data") ?: JSONArray()
                val list = mutableListOf<ChangeLogEntry>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val epoch = runCatching { Instant.parse(obj.optString("logged_at")).epochSecond }.getOrDefault(0L)
                    list.add(
                        ChangeLogEntry(
                            obj.optString("username"),
                            obj.optString("status"),
                            obj.optString("changes"),
                            epoch
                        )
                    )
                }
                list
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addLog(token: String, eventId: Int, log: ChangeLogEntry): Boolean {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/events/$eventId/logs"
        val obj = JSONObject()
        obj.put("status", log.status)
        obj.put("changes", log.changes)
        val request = Request.Builder()
            .url(url)
            .post(obj.toString().toRequestBody(jsonType))
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (_: Exception) { false }
    }
}
