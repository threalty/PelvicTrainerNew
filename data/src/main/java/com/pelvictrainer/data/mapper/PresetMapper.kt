package com.pelvictrainer.data.mapper

import com.pelvictrainer.database.entities.PresetEntity
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.TrainingPreset

fun PresetEntity.toDomain(): TrainingPreset {
    return TrainingPreset(
        id = id,
        name = name,
        description = description,
        level = when (level.uppercase()) {
            "BEGINNER" -> TrainingLevel.BEGINNER
            "INTERMEDIATE" -> TrainingLevel.INTERMEDIATE
            "ADVANCED" -> TrainingLevel.ADVANCED
            else -> TrainingLevel.BEGINNER
        },
        squeezeTime = squeezeTime,
        holdTime = holdTime,
        relaxTime = relaxTime,
        totalReps = totalReps
    )
}