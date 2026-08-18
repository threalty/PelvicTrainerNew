package com.pelvictrainer.domain.model

data class TrainingSession(
    val id: Long,
    val presetId: Long,
    val date: Long,
    val durationSeconds: Long,
    val repeats: Int,
    val synced: Boolean = true,
    val serverSessionId: Int? = null,
)