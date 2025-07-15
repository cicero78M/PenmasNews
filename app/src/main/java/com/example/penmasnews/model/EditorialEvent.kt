package com.example.penmasnews.model

/**
 * Data class representing a single item in the editorial calendar.
 */
data class EditorialEvent(
    val date: String,
    val topic: String,
    val assignee: String,
    val status: String,
    val content: String = "",
    val summary: String = ""
)
