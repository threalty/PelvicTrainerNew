package com.pelvictrainer.data.mapper

import com.pelvictrainer.database.entities.TrainingSessionEntity
import com.pelvictrainer.domain.model.TrainingSession

fun TrainingSessionEntity.toDomain(): TrainingSession {
    return TrainingSession(
        id = id,
        presetId = presetId,
        date = date,
        durationSeconds = durationSeconds,
        repeats = repeats,
        synced = synced,
        serverSessionId = serverSessionId,
    )
}

fun TrainingSession.toEntity(): TrainingSessionEntity {
    return TrainingSessionEntity(
        id = id,
        presetId = presetId,
        date = date,
        durationSeconds = durationSeconds,
        repeats = repeats,
        synced = synced,
        serverSessionId = serverSessionId,
    )
}