package com.example.penmasnews.feature

import android.content.Context
import com.example.penmasnews.BuildConfig
import com.example.penmasnews.model.EditorialEvent
import com.example.penmasnews.model.CMSPrefs
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
    context: Context,
    private val apiKey: String = BuildConfig.BLOGGER_API_KEY,
    private val blogId: String = BuildConfig.BLOGGER_BLOG_ID,
    private val defaultWpBaseUrl: String = BuildConfig.WORDPRESS_BASE_URL,
    private val defaultWpUser: String = BuildConfig.WORDPRESS_USER,
    private val defaultWpAppPass: String = BuildConfig.WORDPRESS_APP_PASS,
) {
    private val client = OkHttpClient()

    private val wpBaseUrl: String
    private val wpUser: String
    private val wpAppPass: String

    init {
        wpBaseUrl = CMSPrefs.getWordpressBaseUrl(context) ?: defaultWpBaseUrl
        wpUser = CMSPrefs.getWordpressUser(context) ?: defaultWpUser
        wpAppPass = CMSPrefs.getWordpressAppPass(context) ?: defaultWpAppPass
    }

    data class PublishResult(val success: Boolean, val raw: String?)

    /**
     * Publish the provided event as a blog post.
     * Returns the API call success state along with the raw response.
     */
    fun publishToBlogspot(event: EditorialEvent, token: String? = null): PublishResult {
        if (blogId.isBlank()) return PublishResult(false, null)

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
            client.newCall(request).execute().use { resp ->
                PublishResult(resp.isSuccessful, resp.body?.string())
            }
        } catch (_: Exception) {
            PublishResult(false, null)
        }
    }

    /**
     * Publish the provided event using the WordPress REST API.
     * Returns the success state and raw response of the call.
     */
    fun publishToWordpress(event: EditorialEvent): PublishResult {
        if (wpBaseUrl.isBlank() || wpUser.isBlank() || wpAppPass.isBlank()) return PublishResult(false, null)

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
            client.newCall(request).execute().use { resp ->
                PublishResult(resp.isSuccessful, resp.body?.string())
            }
        } catch (_: Exception) {
            PublishResult(false, null)
        }
    }
}
