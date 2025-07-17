package com.example.penmasnews.model

/**
 * Data class representing a single item in the editorial calendar.
 */
import java.io.Serializable

data class EditorialEvent(
    val date: String,
    val topic: String,
    val assignee: String,
    var status: String,
    val content: String = "",
    val summary: String = "",
    val imagePath: String = "",
    val id: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val username: String = ""
) : Serializable
