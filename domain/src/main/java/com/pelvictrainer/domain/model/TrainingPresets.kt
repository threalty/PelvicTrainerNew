package com.pelvictrainer.domain.model


object TrainingPresets {


    val beginner =
        TrainingPreset(
            id = "beginner",
            name = "Начальный",
            description = "Мягкая тренировка для привыкания",
            contractSeconds = 3,
            holdSeconds = 3,
            relaxSeconds = 5,
            repeats = 8
        )


    val standard =
        TrainingPreset(
            id = "standard",
            name = "Стандарт",
            description = "Базовая ежедневная тренировка",
            contractSeconds = 5,
            holdSeconds = 5,
            relaxSeconds = 5,
            repeats = 10
        )


    val advanced =
        TrainingPreset(
            id = "advanced",
            name = "Продвинутая",
            description = "Усиленный режим",
            contractSeconds = 7,
            holdSeconds = 7,
            relaxSeconds = 5,
            repeats = 12
        )


    val all =
        listOf(
            beginner,
            standard,
            advanced
        )

}