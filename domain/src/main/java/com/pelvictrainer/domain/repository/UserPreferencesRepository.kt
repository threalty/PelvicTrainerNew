package com.pelvictrainer.domain.repository

import com.pelvictrainer.domain.model.TrainingLevel
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getTrainingLevel(): Flow<TrainingLevel?>
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun updateTrainingLevel(level: TrainingLevel)
    suspend fun completeOnboarding()
}