package com.example.penmasnews.model

/** Combination of an editorial event and its approval request. */
data class ApprovalItem(
    var event: EditorialEvent,
    var request: ApprovalRequest
)
