package com.pelvictrainer.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pelvictrainer.domain.model.AccentColor
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
) : PelvicDataStore {

    private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    private val TRAINING_LEVEL_KEY = stringPreferencesKey("training_level")
    private val USER_AGE_KEY = intPreferencesKey("user_age")
    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    private val ACCENT_COLOR_KEY = stringPreferencesKey("accent_color")
    private val VOICE_ENABLED_KEY = booleanPreferencesKey("voice_enabled")
    private val VOICE_VOLUME_KEY = floatPreferencesKey("voice_volume")
    private val VIBRATION_ENABLED_KEY = booleanPreferencesKey("vibration_enabled")
    private val VIBRATION_INTENSITY_KEY = floatPreferencesKey("vibration_intensity")

    override fun getUserPreferences(): Flow<UserPreferences> {
        return context.dataStore.data.map { prefs ->
            UserPreferences(
                isOnboardingCompleted = prefs[ONBOARDING_COMPLETED_KEY] ?: false,
                trainingLevel = prefs[TRAINING_LEVEL_KEY]?.let {
                    try { TrainingLevel.valueOf(it) } catch (e: Exception) { TrainingLevel.BEGINNER }
                } ?: TrainingLevel.BEGINNER,
                userAge = prefs[USER_AGE_KEY],
                themeMode = prefs[THEME_MODE_KEY]?.let {
                    try { ThemeMode.valueOf(it) } catch (e: Exception) { ThemeMode.DARK }
                } ?: ThemeMode.DARK,
                accentColor = prefs[ACCENT_COLOR_KEY]?.let {
                    try { AccentColor.valueOf(it) } catch (e: Exception) { AccentColor.BORDEAUX }
                } ?: AccentColor.BORDEAUX,
                voiceEnabled = prefs[VOICE_ENABLED_KEY] ?: true,
                voiceVolume = prefs[VOICE_VOLUME_KEY] ?: 0.8f,
                vibrationEnabled = prefs[VIBRATION_ENABLED_KEY] ?: true,
                vibrationIntensity = prefs[VIBRATION_INTENSITY_KEY] ?: 0.8f
            )
        }
    }

    override fun getTrainingLevel(): Flow<TrainingLevel?> {
        return context.dataStore.data.map { prefs ->
            prefs[TRAINING_LEVEL_KEY]?.let {
                try { TrainingLevel.valueOf(it) } catch (e: Exception) { null }
            }
        }
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] ?: false
        }
    }

    override suspend fun updateTrainingLevel(level: TrainingLevel) {
        context.dataStore.edit { prefs ->
            prefs[TRAINING_LEVEL_KEY] = level.name
        }
    }

    override suspend fun completeOnboarding() {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED_KEY] = true
        }
    }

    override suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE_KEY] = mode.name
        }
    }

    override suspend fun updateAccentColor(color: AccentColor) {
        context.dataStore.edit { prefs ->
            prefs[ACCENT_COLOR_KEY] = color.name
        }
    }

    override suspend fun updateVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[VOICE_ENABLED_KEY] = enabled
        }
    }

    override suspend fun updateVoiceVolume(volume: Float) {
        context.dataStore.edit { prefs ->
            prefs[VOICE_VOLUME_KEY] = volume
        }
    }

    override suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[VIBRATION_ENABLED_KEY] = enabled
        }
    }

    override suspend fun updateVibrationIntensity(intensity: Float) {
        context.dataStore.edit { prefs ->
            prefs[VIBRATION_INTENSITY_KEY] = intensity
        }
    }
}