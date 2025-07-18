package com.example.penmasnews.feature

import com.example.penmasnews.BuildConfig
import com.example.penmasnews.model.EditorialEvent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Helper class for publishing approved content to Blogspot via Blogger API.
 */
class CMSIntegration(
    private val apiKey: String = BuildConfig.BLOGGER_API_KEY,
    private val blogId: String = BuildConfig.BLOGGER_BLOG_ID,
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
}
