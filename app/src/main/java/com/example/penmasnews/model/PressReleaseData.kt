package com.example.penmasnews.model

import java.io.Serializable

/** Data stored for press release details associated with an event */
data class PressReleaseData(
    val eventId: Int,
    val judul: String,
    val dasar: String,
    val tersangka: String,
    val tkp: String,
    val kronologi: String,
    val modus: String,
    val barangBukti: String,
    val pasal: String,
    val ancaman: String,
    val catatan: String
) : Serializable
