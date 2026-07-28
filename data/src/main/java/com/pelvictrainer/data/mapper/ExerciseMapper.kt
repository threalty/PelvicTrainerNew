package com.pelvictrainer.data.mapper

import com.pelvictrainer.database.entities.ExerciseEntity
import com.pelvictrainer.domain.model.Exercise


fun ExerciseEntity.toDomain(): Exercise {

    return Exercise(

        contractSeconds = contractSeconds,

        holdSeconds = holdSeconds,

        relaxSeconds = relaxSeconds,

        repeats = repeats

    )
}



fun Exercise.toEntity(
    name: String = "",
    description: String = "",
    orderIndex: Int = 0
): ExerciseEntity {

    return ExerciseEntity(

        name = name,

        description = description,

        contractSeconds = contractSeconds,

        holdSeconds = holdSeconds,

        relaxSeconds = relaxSeconds,

        repeats = repeats,

        orderIndex = orderIndex

    )
}