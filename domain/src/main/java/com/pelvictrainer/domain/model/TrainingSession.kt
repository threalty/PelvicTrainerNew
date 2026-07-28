package com.pelvictrainer.domain.model


data class TrainingSession(

    val id: Long = 0,

    val date: Long,

    val durationSeconds: Int,

    val repeats: Int,

    val presetId: String

)