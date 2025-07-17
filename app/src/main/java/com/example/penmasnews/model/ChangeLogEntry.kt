package com.example.penmasnews.model

/** Data class capturing an edit log entry */
data class ChangeLogEntry(
    val user: String,
    val status: String,
    val changes: String,
    /** Unix timestamp (seconds since epoch) */
    val timestamp: Long
)
