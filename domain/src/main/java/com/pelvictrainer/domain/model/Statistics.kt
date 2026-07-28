package com.pelvictrainer.domain.model


data class Statistics(

    val totalSessions: Int,

    val completedSessions: Int,

    val totalTrainingSeconds: Long,

    val averageSessionSeconds: Int

)