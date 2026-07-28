package com.pelvictrainer.domain.model


data class UserPreferences(

    val trainingLevel: TrainingLevel = TrainingLevel.BEGINNER,

    val notificationsEnabled: Boolean = true

)