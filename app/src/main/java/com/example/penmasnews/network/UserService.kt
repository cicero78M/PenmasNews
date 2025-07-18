package com.example.penmasnews.network

import com.example.penmasnews.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/** Helper to fetch user profile information */
object UserService {
    private val client = OkHttpClient()

    fun fetchUsername(token: String, userId: String): String? {
        if (userId.isBlank()) return null
        val url = BuildConfig.API_BASE_URL.trimEnd('/') + "/api/users/$userId"
        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string() ?: return null
                if (!resp.isSuccessful) return null
                val json = JSONObject(body)
                json.optJSONObject("data")?.optString("username")
                    ?: json.optString("username", null)
            }
        } catch (_: Exception) {
            null
        }
    }
}
