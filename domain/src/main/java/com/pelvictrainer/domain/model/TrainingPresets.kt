package com.pelvictrainer.domain.model

object TrainingPresets {

    val allPresets = listOf(
        TrainingPreset(
            id = 1L,
            name = "Начинающий",
            level = TrainingLevel.BEGINNER,
            squeezeTime = 3,
            holdTime = 3,
            relaxTime = 5,
            totalReps = 10
        ),
        TrainingPreset(
            id = 2L,
            name = "Средний",
            level = TrainingLevel.INTERMEDIATE,
            squeezeTime = 5,
            holdTime = 5,
            relaxTime = 5,
            totalReps = 15
        ),
        TrainingPreset(
            id = 3L,
            name = "Продвинутый",
            level = TrainingLevel.ADVANCED,
            squeezeTime = 10,
            holdTime = 10,
            relaxTime = 5,
            totalReps = 20
        )
    )

    fun getById(id: Long): TrainingPreset? {
        return allPresets.find { it.id == id }
    }
}