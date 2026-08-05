package com.pelvictrainer.domain.model

data class TrainingPreset(
    val id: Long,
    val name: String,
    val level: TrainingLevel,
    val squeezeTime: Int,      // Время сжатия в секундах
    val holdTime: Int,         // Время удержания в секундах
    val relaxTime: Int,        // Время расслабления в секундах
    val totalReps: Int,        // Общее количество повторений
    val restBetweenReps: Int = 5 // Отдых между повторениями (опционально)
)