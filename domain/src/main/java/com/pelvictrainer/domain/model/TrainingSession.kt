package com.pelvictrainer.domain.model

data class TrainingSession(
    val id: Long,
    val presetId: Long,
    val date: Long,
    val durationSeconds: Long,
    val repeats: Int
)