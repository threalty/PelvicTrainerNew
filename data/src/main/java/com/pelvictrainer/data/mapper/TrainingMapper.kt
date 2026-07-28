package com.pelvictrainer.data.mapper


import com.pelvictrainer.database.entities.TrainingSessionEntity
import com.pelvictrainer.domain.model.TrainingSession



fun TrainingSessionEntity.toDomain(): TrainingSession {

    return TrainingSession(

        id = id,

        date = date,

        durationSeconds = durationSeconds,

        repeats = repeats,

        presetId = presetId

    )

}



fun TrainingSession.toEntity(): TrainingSessionEntity {

    return TrainingSessionEntity(

        id = id,

        date = date,

        durationSeconds = durationSeconds,

        repeats = repeats,

        presetId = presetId

    )

}