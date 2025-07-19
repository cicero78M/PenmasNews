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
                DebugLogger.log(context, "WordPress login failed: ${'$'}{e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string()
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
}
