package com.example.penmasnews.network

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Utility service that fetches titles from Tribrata News RSS feed
 * and generates word frequencies for a simple trend analysis.
 */
object AnalyticsService {
    private val client = OkHttpClient()

    private const val FEED_URL = "https://tribratanews.jatim.polri.go.id/feed"

    /**
     * Retrieve recent article titles from the RSS feed.
     */
    fun fetchTitles(): List<String> {
        val request = Request.Builder().url(FEED_URL).build()
        return try {
            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string() ?: return emptyList()
                // simple regex parse of <title> elements
                Regex("<title>(.*?)</title>").findAll(body)
                    .map { it.groupValues[1] }
                    .drop(1) // skip channel title
                    .toList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Return a map of word -> frequency from recent titles.
     */
    fun fetchWordFrequency(): Map<String, Int> {
        val titles = fetchTitles()
        val freq = mutableMapOf<String, Int>()
        val wordRegex = Regex("[A-Za-zÀ-ÿ']+")
        for (t in titles) {
            wordRegex.findAll(t.lowercase()).forEach {
                val w = it.value
                if (w.length < 3) return@forEach
                freq[w] = (freq[w] ?: 0) + 1
            }
        }
        return freq.entries.sortedByDescending { it.value }
            .take(30)
            .associate { it.toPair() }
    }
}
