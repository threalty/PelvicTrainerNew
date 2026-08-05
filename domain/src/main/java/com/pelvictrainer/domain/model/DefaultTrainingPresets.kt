package com.pelvictrainer.domain.model

object DefaultTrainingPresets {

    fun getBeginner(): TrainingPreset = TrainingPreset(
        id = 1L,
        name = "Новичок",
        description = "Базовый уровень для начала тренировок",
        level = TrainingLevel.BEGINNER,
        squeezeTime = 3,
        holdTime = 3,
        relaxTime = 5,
        totalReps = 10
    )

    fun getIntermediate(): TrainingPreset = TrainingPreset(
        id = 2L,
        name = "Любитель",
        description = "Средний уровень сложности",
        level = TrainingLevel.INTERMEDIATE,
        squeezeTime = 5,
        holdTime = 5,
        relaxTime = 5,
        totalReps = 15
    )

    fun getAdvanced(): TrainingPreset = TrainingPreset(
        id = 3L,
        name = "Профи",
        description = "Максимальная нагрузка",
        level = TrainingLevel.ADVANCED,
        squeezeTime = 8,
        holdTime = 8,
        relaxTime = 4,
        totalReps = 20
    )

    fun getAll(): List<TrainingPreset> = listOf(
        getBeginner(),
        getIntermediate(),
        getAdvanced()
    )
}