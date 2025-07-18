package com.example.penmasnews.network

import com.example.penmasnews.BuildConfig
import com.example.penmasnews.model.ApprovalRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/** Helper for interacting with approval request endpoints. */
object ApprovalService {
    private val client = OkHttpClient()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    fun fetchApprovals(token: String): List<ApprovalRequest> {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/approvals"
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string() ?: return emptyList()
                if (!resp.isSuccessful) return emptyList()
                val array = JSONObject(body).optJSONArray("data") ?: JSONArray()
                val list = mutableListOf<ApprovalRequest>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        ApprovalRequest(
                            obj.optInt("request_id"),
                            obj.optInt("event_id"),
                            obj.optString("requested_by"),
                            obj.optString("status"),
                            obj.optString("created_at"),
                            obj.optString("updated_at"),
                        )
                    )
                }
                list
            }
        } catch (_: Exception) { emptyList() }
    }

    fun createApproval(token: String, eventId: Int): ApprovalRequest? {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/approvals"
        val obj = JSONObject()
        obj.put("event_id", eventId)
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
                ApprovalRequest(
                    json.optInt("request_id"),
                    json.optInt("event_id"),
                    json.optString("requested_by"),
                    json.optString("status"),
                    json.optString("created_at"),
                    json.optString("updated_at"),
                )
            }
        } catch (_: Exception) { null }
    }

    fun updateApproval(token: String, id: Int, status: String): Boolean {
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/approvals/$id"
        val obj = JSONObject()
        obj.put("status", status)
        val request = Request.Builder()
            .url(url)
            .put(obj.toString().toRequestBody(jsonType))
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (_: Exception) { false }
    }
}
