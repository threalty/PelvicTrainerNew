package com.pelvictrainer.domain.model

/**
 * Настройка напоминания о тренировке.
 * @param hour час (0-23)
 * @param minute минута (0-59)
 */
data class ReminderConfig(
    val hour: Int,
    val minute: Int
) {
    /** Возвращает время в формате "HH:mm" для отображения */
    fun formatTime(): String {
        return String.format("%02d:%02d", hour, minute)
    }

    /** Уникальный ID для WorkManager (используется как тег) */
    fun toWorkTag(): String {
        return "reminder_${hour}_${minute}"
    }

    companion object {
        /** Парсинг из строки формата "HH:mm" */
        fun fromString(value: String): ReminderConfig? {
            val parts = value.split(":")
            if (parts.size != 2) return null
            return try {
                ReminderConfig(
                    hour = parts[0].toInt().coerceIn(0, 23),
                    minute = parts[1].toInt().coerceIn(0, 59)
                )
            } catch (e: NumberFormatException) {
                null
            }
        }
    }
}