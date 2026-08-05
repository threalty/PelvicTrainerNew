package com.pelvictrainer.domain.repository

import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>
    fun getTrainingLevel(): Flow<TrainingLevel?>
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun updateTrainingLevel(level: TrainingLevel)
    suspend fun completeOnboarding()
}