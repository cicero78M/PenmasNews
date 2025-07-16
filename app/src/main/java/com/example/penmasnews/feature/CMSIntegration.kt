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
    fun publishToBlogspot(event: EditorialEvent): Boolean {
        if (apiKey.isBlank() || blogId.isBlank()) return false
        val url = "https://www.googleapis.com/blogger/v3/blogs/$blogId/posts/?key=$apiKey"
        val obj = JSONObject()
        obj.put("kind", "blogger#post")
        obj.put("title", event.topic)
        obj.put("content", if (event.content.isNotBlank()) event.content else event.summary)
        val body = obj.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).execute().use { resp ->
            return resp.isSuccessful
        }
    }
}
