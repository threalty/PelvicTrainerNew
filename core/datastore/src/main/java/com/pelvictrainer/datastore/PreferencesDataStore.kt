package com.pelvictrainer.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pelvictrainer.domain.model.AccentColor
import com.pelvictrainer.domain.model.BackgroundSound
import com.pelvictrainer.domain.model.ReminderConfig
import com.pelvictrainer.domain.model.ThemeMode
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "pelvic_prefs")

@Singleton
class PreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")
    private val trainingLevelKey = stringPreferencesKey("training_level")
    private val userAgeKey = intPreferencesKey("user_age")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val accentColorKey = stringPreferencesKey("accent_color")
    private val voiceEnabledKey = booleanPreferencesKey("voice_enabled")
    private val voiceVolumeKey = floatPreferencesKey("voice_volume")
    private val vibrationEnabledKey = booleanPreferencesKey("vibration_enabled")
    private val vibrationIntensityKey = floatPreferencesKey("vibration_intensity")
    private val remindersEnabledKey = booleanPreferencesKey("reminders_enabled")
    private val reminderTimesKey = stringSetPreferencesKey("reminder_times")
    private val reminderDaysKey = stringPreferencesKey("reminder_days")
    private val weeklyGoalKey = intPreferencesKey("weekly_goal")
    private val backgroundSoundKey = stringPreferencesKey("background_sound")

    fun getUserPreferences(): Flow<UserPreferences> {
        return context.dataStore.data.map { prefs ->
            val reminderTimes = prefs[reminderTimesKey]?.mapNotNull {
                ReminderConfig.fromString(it)
            } ?: emptyList()

            val reminderDays = prefs[reminderDaysKey]?.split(",")?.mapNotNull {
                it.trim().toIntOrNull()
            }?.filter { it in 1..7 } ?: listOf(1, 2, 3, 4, 5, 6, 7)

            UserPreferences(
                isOnboardingCompleted = prefs[onboardingCompletedKey] ?: false,
                trainingLevel = prefs[trainingLevelKey]?.let {
                    try { TrainingLevel.valueOf(it) } catch (e: Exception) { TrainingLevel.BEGINNER }
                } ?: TrainingLevel.BEGINNER,
                userAge = prefs[userAgeKey],
                themeMode = prefs[themeModeKey]?.let {
                    try { ThemeMode.valueOf(it) } catch (e: Exception) { ThemeMode.DARK }
                } ?: ThemeMode.DARK,
                accentColor = prefs[accentColorKey]?.let {
                    try { AccentColor.valueOf(it) } catch (e: Exception) { AccentColor.BORDEAUX }
                } ?: AccentColor.BORDEAUX,
                voiceEnabled = prefs[voiceEnabledKey] ?: true,
                voiceVolume = prefs[voiceVolumeKey] ?: 0.8f,
                vibrationEnabled = prefs[vibrationEnabledKey] ?: true,
                vibrationIntensity = prefs[vibrationIntensityKey] ?: 0.8f,
                remindersEnabled = prefs[remindersEnabledKey] ?: false,
                reminderTimes = reminderTimes,
                reminderDaysOfWeek = reminderDays,
                weeklyGoal = prefs[weeklyGoalKey]?.coerceIn(1, 7) ?: 3,
                backgroundSound = prefs[backgroundSoundKey]?.let {
                    try { BackgroundSound.valueOf(it) } catch (e: Exception) { BackgroundSound.NONE }
                } ?: BackgroundSound.NONE
            )
        }
    }

    fun getTrainingLevel(): Flow<TrainingLevel?> {
        return context.dataStore.data.map { prefs ->
            prefs[trainingLevelKey]?.let {
                try { TrainingLevel.valueOf(it) } catch (e: Exception) { null }
            }
        }
    }

    fun isOnboardingCompleted(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[onboardingCompletedKey] ?: false
        }
    }

    suspend fun updateTrainingLevel(level: TrainingLevel) {
        context.dataStore.edit { prefs -> prefs[trainingLevelKey] = level.name }
    }

    suspend fun completeOnboarding() {
        context.dataStore.edit { prefs -> prefs[onboardingCompletedKey] = true }
    }

    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs -> prefs[themeModeKey] = mode.name }
    }

    suspend fun updateAccentColor(color: AccentColor) {
        context.dataStore.edit { prefs -> prefs[accentColorKey] = color.name }
    }

    suspend fun updateVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[voiceEnabledKey] = enabled }
    }

    suspend fun updateVoiceVolume(volume: Float) {
        context.dataStore.edit { prefs -> prefs[voiceVolumeKey] = volume }
    }

    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[vibrationEnabledKey] = enabled }
    }

    suspend fun updateVibrationIntensity(intensity: Float) {
        context.dataStore.edit { prefs -> prefs[vibrationIntensityKey] = intensity }
    }

    suspend fun updateRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[remindersEnabledKey] = enabled }
    }

    suspend fun addReminderTime(config: ReminderConfig) {
        context.dataStore.edit { prefs ->
            val current = prefs[reminderTimesKey]?.toMutableSet() ?: mutableSetOf()
            current.add(config.formatTime())
            prefs[reminderTimesKey] = current
        }
    }

    suspend fun removeReminderTime(config: ReminderConfig) {
        context.dataStore.edit { prefs ->
            val current = prefs[reminderTimesKey]?.toMutableSet() ?: mutableSetOf()
            current.remove(config.formatTime())
            prefs[reminderTimesKey] = current
        }
    }

    suspend fun updateReminderDaysOfWeek(days: List<Int>) {
        context.dataStore.edit { prefs ->
            prefs[reminderDaysKey] = days.filter { it in 1..7 }.joinToString(",")
        }
    }

    suspend fun updateWeeklyGoal(goal: Int) {
        context.dataStore.edit { prefs ->
            prefs[weeklyGoalKey] = goal.coerceIn(1, 7)
        }
    }

    suspend fun updateBackgroundSound(sound: BackgroundSound) {
        context.dataStore.edit { prefs ->
            prefs[backgroundSoundKey] = sound.name
        }
    }
}