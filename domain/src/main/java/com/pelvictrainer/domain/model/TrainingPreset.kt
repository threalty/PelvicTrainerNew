package com.pelvictrainer.domain.model

data class TrainingPreset(
    val id: Long,
    val name: String,
    val level: TrainingLevel,
    val squeezeTime: Int,      // Время сжатия (сек)
    val holdTime: Int,         // Время удержания (сек)
    val relaxTime: Int,        // Время расслабления (сек)
    val totalReps: Int         // Количество повторений
)