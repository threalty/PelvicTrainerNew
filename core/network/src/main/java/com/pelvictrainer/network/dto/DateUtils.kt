package com.pelvictrainer.network.dto

import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun String.toEpochMillis(): Long {
    return try {
        val zoned = ZonedDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
        zoned.toInstant().toEpochMilli()
    } catch (e: Exception) {
        try {
            Instant.parse(this).toEpochMilli()
        } catch (e2: Exception) {
            System.currentTimeMillis()
        }
    }
}
