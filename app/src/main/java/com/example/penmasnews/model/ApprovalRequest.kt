package com.example.penmasnews.model

import java.io.Serializable

/** Data class representing an approval request from the backend. */
data class ApprovalRequest(
    val requestId: Int,
    val eventId: Int,
    val requestedBy: String,
    var status: String,
    val createdAt: String = "",
    val updatedAt: String = "",
) : Serializable
