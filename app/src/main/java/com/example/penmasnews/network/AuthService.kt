package com.example.penmasnews.network

import com.example.penmasnews.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object AuthService {
    private val client = OkHttpClient()
    private val jsonType = "application/json; charset=utf-8".toMediaType()

    data class Result(val success: Boolean, val token: String? = null, val message: String? = null, val role: String? = null)

    fun login(username: String, password: String): Result {
        val url = BuildConfig.BACKEND_BASE_URL.trimEnd('/') + "/auth/penmas-login"
        val obj = JSONObject()
        obj.put("username", username)
        obj.put("password", password)
        val request = Request.Builder()
            .url(url)
            .post(obj.toString().toRequestBody(jsonType))
            .build()
        return try {
            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string()
                if (!resp.isSuccessful || body == null) {
                    val msg = try { JSONObject(body ?: "{}").optString("message") } catch (_: Exception) { null }
                    return Result(false, message = msg)
                }
                val json = JSONObject(body)
                Result(
                    json.optBoolean("success"),
                    json.optString("token"),
                    json.optString("message", null),
                    json.optJSONObject("user")?.optString("role")
                        ?: json.optJSONObject("client")?.optString("role")
                )
            }
        } catch (e: javax.net.ssl.SSLException) {
            Result(false, message = "TLS handshake failed: ${e.message}")
        } catch (e: java.io.IOException) {
            Result(false, message = e.message)
        }
    }

    fun signup(username: String, password: String, role: String): Result {
        val url = BuildConfig.BACKEND_BASE_URL.trimEnd('/') + "/auth/penmas-register"
        val obj = JSONObject()
        obj.put("username", username)
        obj.put("password", password)
        obj.put("role", role)
        val request = Request.Builder()
            .url(url)
            .post(obj.toString().toRequestBody(jsonType))
            .build()
        return try {
            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string()
                if (!resp.isSuccessful || body == null) {
                    val msg = try { JSONObject(body ?: "{}").optString("message") } catch (_: Exception) { null }
                    return Result(false, message = msg)
                }
                val json = JSONObject(body)
                Result(json.optBoolean("success"), json.optString("token"), json.optString("message"))
            }
        } catch (e: javax.net.ssl.SSLException) {
            Result(false, message = "TLS handshake failed: ${e.message}")
        } catch (e: java.io.IOException) {
            Result(false, message = e.message)
        }
    }
}
