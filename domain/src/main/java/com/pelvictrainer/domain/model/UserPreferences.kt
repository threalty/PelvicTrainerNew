package com.pelvictrainer.domain.model

data class UserPreferences(
    val isOnboardingCompleted: Boolean = false,
    val trainingLevel: TrainingLevel = TrainingLevel.BEGINNER,
    val userAge: Int? = null
)