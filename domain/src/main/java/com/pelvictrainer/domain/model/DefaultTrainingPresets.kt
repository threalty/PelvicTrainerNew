package com.pelvictrainer.domain.model


object DefaultTrainingPresets {


    val beginner =
        TrainingPreset(
            id = "beginner",
            name = "Начальный",
            description = "Адаптация и контроль мышц",
            contractSeconds = 3,
            holdSeconds = 3,
            relaxSeconds = 5,
            repeats = 8
        )


    val normal =
        TrainingPreset(
            id = "normal",
            name = "Стандарт",
            description = "Основная ежедневная тренировка",
            contractSeconds = 5,
            holdSeconds = 5,
            relaxSeconds = 5,
            repeats = 10
        )


    val advanced =
        TrainingPreset(
            id = "advanced",
            name = "Продвинутый",
            description = "Максимальная нагрузка",
            contractSeconds = 7,
            holdSeconds = 7,
            relaxSeconds = 5,
            repeats = 12
        )


    val all =
        listOf(
            beginner,
            normal,
            advanced
        )

}