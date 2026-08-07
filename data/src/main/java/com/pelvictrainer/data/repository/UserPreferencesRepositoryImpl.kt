package com.pelvictrainer.data.repository

import com.pelvictrainer.datastore.PelvicDataStore
import com.pelvictrainer.domain.model.AccentColor
import com.pelvictrainer.domain.model.ThemeMode
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.UserPreferences
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: PelvicDataStore
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences>
        get() = dataStore.getUserPreferences()

    override fun getTrainingLevel(): Flow<TrainingLevel?> = dataStore.getTrainingLevel()

    override fun isOnboardingCompleted(): Flow<Boolean> = dataStore.isOnboardingCompleted()

    override suspend fun updateTrainingLevel(level: TrainingLevel) =
        dataStore.updateTrainingLevel(level)

    override suspend fun completeOnboarding() = dataStore.completeOnboarding()

    override suspend fun updateThemeMode(mode: ThemeMode) = dataStore.updateThemeMode(mode)

    override suspend fun updateAccentColor(color: AccentColor) = dataStore.updateAccentColor(color)

    override suspend fun updateVoiceEnabled(enabled: Boolean) = dataStore.updateVoiceEnabled(enabled)

    override suspend fun updateVoiceVolume(volume: Float) = dataStore.updateVoiceVolume(volume)

    override suspend fun updateVibrationEnabled(enabled: Boolean) =
        dataStore.updateVibrationEnabled(enabled)

    override suspend fun updateVibrationIntensity(intensity: Float) =
        dataStore.updateVibrationIntensity(intensity)
}