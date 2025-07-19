package com.example.penmasnews.util

/** Helpers for normalizing user provided URLs. */
object UrlUtils {
    /** Ensure the given URL starts with an http or https scheme. */
    fun ensureHttpScheme(raw: String): String {
        val trimmed = raw.trim()
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }
}
