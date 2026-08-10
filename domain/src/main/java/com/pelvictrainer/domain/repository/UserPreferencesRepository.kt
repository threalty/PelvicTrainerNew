package com.pelvictrainer.domain.repository

import com.pelvictrainer.domain.model.AccentColor
import com.pelvictrainer.domain.model.BackgroundSound
import com.pelvictrainer.domain.model.ReminderConfig
import com.pelvictrainer.domain.model.ThemeMode
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    fun getTrainingLevel(): Flow<TrainingLevel?>
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun updateTrainingLevel(level: TrainingLevel)
    suspend fun completeOnboarding()
    suspend fun updateThemeMode(mode: ThemeMode)
    suspend fun updateAccentColor(color: AccentColor)
    suspend fun updateVoiceEnabled(enabled: Boolean)
    suspend fun updateVoiceVolume(volume: Float)
    suspend fun updateVibrationEnabled(enabled: Boolean)
    suspend fun updateVibrationIntensity(intensity: Float)

    suspend fun updateRemindersEnabled(enabled: Boolean)
    suspend fun addReminderTime(config: ReminderConfig)
    suspend fun removeReminderTime(config: ReminderConfig)
    suspend fun updateReminderDaysOfWeek(days: List<Int>)

    suspend fun updateWeeklyGoal(goal: Int)
    suspend fun updateBackgroundSound(sound: BackgroundSound)
}