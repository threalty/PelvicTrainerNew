package com.pelvictrainer.domain.model

data class UserPreferences(
    val isOnboardingCompleted: Boolean = false,
    val trainingLevel: TrainingLevel = TrainingLevel.BEGINNER,
    val userAge: Int? = null,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val accentColor: AccentColor = AccentColor.BORDEAUX,
    val voiceEnabled: Boolean = true,
    val voiceVolume: Float = 0.8f,
    val vibrationEnabled: Boolean = true,
    val vibrationIntensity: Float = 0.8f
)