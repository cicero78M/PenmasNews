package com.example.penmasnews.feature

import android.content.Context
import com.example.penmasnews.model.CMSPrefs
import com.example.penmasnews.util.DebugLogger
import com.example.penmasnews.util.UrlUtils
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

object WordpressAuth {
    fun login(context: Context, baseUrl: String, user: String, pass: String, callback: (String?) -> Unit) {
        val normalized = UrlUtils.ensureHttpScheme(baseUrl)
        val url = normalized.trimEnd('/') + "/wp-json/jwt-auth/v1/token"
        val form = FormBody.Builder()
            .add("username", user)
            .add("password", pass)
            .build()
        val request = Request.Builder().url(url).post(form).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DebugLogger.log(context, "WordPress login failed: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string()
                DebugLogger.log(context, "WordPress login response: ${bodyStr ?: "null"}")
                val token = try {
                    JSONObject(bodyStr ?: "{}").getString("token")
                } catch (_: Exception) { null }
                if (token != null) {
                    CMSPrefs.saveWordpressToken(context, token)
                }
                callback(token)
            }
        })
    }

    /**
     * Perform a blocking login request. Useful for workflow automation when the
     * token is needed synchronously.
     */
    fun loginBlocking(context: Context, baseUrl: String, user: String, pass: String): String? {
        val normalized = UrlUtils.ensureHttpScheme(baseUrl)
        val url = normalized.trimEnd('/') + "/wp-json/jwt-auth/v1/token"
        val form = FormBody.Builder()
            .add("username", user)
            .add("password", pass)
            .build()
        val request = Request.Builder().url(url).post(form).build()
        return try {
            OkHttpClient().newCall(request).execute().use { response ->
                val bodyStr = response.body?.string()
                DebugLogger.log(context, "WordPress login sync response: ${'$'}{bodyStr ?: "null"}")
                val token = try {
                    JSONObject(bodyStr ?: "{}").getString("token")
                } catch (_: Exception) {
                    null
                }
                if (token != null) {
                    CMSPrefs.saveWordpressToken(context, token)
                }
                token
            }
        } catch (e: Exception) {
            DebugLogger.log(context, "WordPress sync login failed: ${'$'}{e.message}")
            null
        }
    }

    fun verifyAppPassword(context: Context, baseUrl: String, user: String, appPass: String, callback: (Boolean) -> Unit) {
        val normalized = UrlUtils.ensureHttpScheme(baseUrl)
        val url = normalized.trimEnd('/') + "/wp-json/wp/v2/users/me"
        val credential = Credentials.basic(user, appPass)
        val request = Request.Builder()
            .url(url)
            .header("Authorization", credential)
            .get()
            .build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DebugLogger.log(context, "WordPress verify failed: ${'$'}{e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string()
                DebugLogger.log(context, "WordPress verify response: ${'$'}{bodyStr ?: "null"}")
                callback(response.isSuccessful)
            }
        })
    }
}
