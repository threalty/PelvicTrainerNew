package com.pelvictrainer.domain.model

object WorkoutLibrary {

    val Beginner = Workout(

        id = "beginner",

        title = "Новичок",

        description = "Базовая тренировка мышц тазового дна",

        exercises = listOf(

            Exercise(
                contractSeconds = 5,
                holdSeconds = 3,
                relaxSeconds = 5,
                repeats = 10
            )

        )

    )



    val Endurance = Workout(

        id = "endurance",

        title = "Выносливость",

        description = "Длительные удержания",

        exercises = listOf(

            Exercise(
                contractSeconds = 8,
                holdSeconds = 10,
                relaxSeconds = 8,
                repeats = 12
            )

        )

    )



    val EjaculationControl = Workout(

        id = "control",

        title = "Контроль возбуждения",

        description = "Улучшение контроля эякуляции",

        exercises = listOf(

            Exercise(
                contractSeconds = 3,
                holdSeconds = 12,
                relaxSeconds = 6,
                repeats = 15
            )

        )

    )



    val QuickContractions = Workout(

        id = "quick",

        title = "Быстрые сокращения",

        description = "Развитие скорости сокращения",

        exercises = listOf(

            Exercise(
                contractSeconds = 1,
                holdSeconds = 0,
                relaxSeconds = 1,
                repeats = 40
            )

        )

    )



    val All = listOf(

        Beginner,

        Endurance,

        EjaculationControl,

        QuickContractions

    )

}