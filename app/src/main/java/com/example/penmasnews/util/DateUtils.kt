package com.example.penmasnews.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/** Utility helpers for consistent date handling */
object DateUtils {
    const val DATE_FORMAT = "dd/MM/yyyy"
    const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

    private val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern(DATETIME_FORMAT)

    /**
     * Format the current time using [DATETIME_FORMAT].
     */
    fun now(): String = LocalDateTime.now().format(dateTimeFormatter)

    /**
     * Format a Unix timestamp (seconds) using [DATETIME_FORMAT] in Asia/Jakarta timezone.
     */
    fun formatTimestamp(epochSeconds: Long): String {
        val zoned = Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.of("Asia/Jakarta"))
        return dateTimeFormatter.format(zoned)
    }

    /**
     * Normalize a date string to [DATETIME_FORMAT]. Accepts either the same
     * format or an ISO-8601 string returned by the backend.
     */
    fun formatDateTime(raw: String): String {
        return try {
            if (raw.contains('T')) {
                val instant = Instant.parse(raw)
                dateTimeFormatter.format(instant.atZone(ZoneId.of("Asia/Jakarta")))
            } else {
                LocalDateTime.parse(raw, dateTimeFormatter).format(dateTimeFormatter)
            }
        } catch (_: Exception) {
            raw
        }
    }

    /**
     * Convert a date in [DATE_FORMAT] to "EEEE, dd/MM/yyyy" localized in Indonesian.
     */
    fun formatDayDate(raw: String): String {
        return try {
            val localDate = LocalDate.parse(raw, DateTimeFormatter.ofPattern(DATE_FORMAT))
            val day = localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
            "$day, ${localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT))}"
        } catch (_: Exception) {
            raw
        }
    }
}
