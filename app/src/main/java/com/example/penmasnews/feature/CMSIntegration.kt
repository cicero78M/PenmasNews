package com.example.penmasnews.feature

import com.example.penmasnews.BuildConfig
import com.example.penmasnews.model.EditorialEvent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Credentials
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Helper class for publishing approved content to Blogspot via Blogger API.
 */
class CMSIntegration(
    private val apiKey: String = BuildConfig.BLOGGER_API_KEY,
    private val blogId: String = BuildConfig.BLOGGER_BLOG_ID,
    private val wpBaseUrl: String = BuildConfig.WORDPRESS_BASE_URL,
    private val wpUser: String = BuildConfig.WORDPRESS_USER,
    private val wpAppPass: String = BuildConfig.WORDPRESS_APP_PASS,
) {
    private val client = OkHttpClient()

    /**
     * Publish the provided event as a blog post.
     * Returns true if the request succeeded.
     */
    fun publishToBlogspot(event: EditorialEvent, token: String? = null): Boolean {
        if (blogId.isBlank()) return false

        val baseUrl = "https://www.googleapis.com/blogger/v3/blogs/$blogId/posts/"
        val url = if (token.isNullOrBlank()) "$baseUrl?key=$apiKey" else baseUrl

        val obj = JSONObject()
        obj.put("kind", "blogger#post")
        obj.put("title", event.topic)

        val contentBuilder = StringBuilder()
        if (event.imagePath.isNotBlank()) {
            contentBuilder.append("<img src=\"")
            contentBuilder.append(event.imagePath)
            contentBuilder.append("\"/>")
        }
        contentBuilder.append(
            if (event.content.isNotBlank()) event.content else event.summary
        )
        obj.put("content", contentBuilder.toString())

        val body = obj.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val builder = Request.Builder()
            .url(url)
            .post(body)
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }

        val request = builder.build()
        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Publish the provided event using the WordPress REST API.
     * Returns true when the API call is successful.
     */
    fun publishToWordpress(event: EditorialEvent): Boolean {
        if (wpBaseUrl.isBlank() || wpUser.isBlank() || wpAppPass.isBlank()) return false

        val url = wpBaseUrl.trimEnd('/') + "/wp-json/wp/v2/posts"

        val obj = JSONObject()
        obj.put("title", event.topic)
        obj.put("status", "publish")
        obj.put("content", if (event.content.isNotBlank()) event.content else event.summary)

        val body = obj.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val credential = Credentials.basic(wpUser, wpAppPass)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", credential)
            .build()

        return try {
            client.newCall(request).execute().use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }
}
