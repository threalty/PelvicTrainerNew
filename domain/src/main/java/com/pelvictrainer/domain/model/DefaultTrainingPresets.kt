package com.pelvictrainer.domain.model

object DefaultTrainingPresets {

    fun getPresetsForLevel(level: TrainingLevel): List<TrainingPreset> {
        return when (level) {
            TrainingLevel.BEGINNER -> listOf(beginnerPreset)
            TrainingLevel.INTERMEDIATE -> listOf(intermediatePreset)
            TrainingLevel.ADVANCED -> listOf(advancedPreset)
        }
    }

    val beginnerPreset = TrainingPreset(
        id = 1L,
        name = "Начинающий",
        level = TrainingLevel.BEGINNER,
        squeezeTime = 3,
        holdTime = 3,
        relaxTime = 5,
        totalReps = 10
    )

    val intermediatePreset = TrainingPreset(
        id = 2L,
        name = "Средний",
        level = TrainingLevel.INTERMEDIATE,
        squeezeTime = 5,
        holdTime = 5,
        relaxTime = 5,
        totalReps = 15
    )

    val advancedPreset = TrainingPreset(
        id = 3L,
        name = "Продвинутый",
        level = TrainingLevel.ADVANCED,
        squeezeTime = 10,
        holdTime = 10,
        relaxTime = 5,
        totalReps = 20
    )
}