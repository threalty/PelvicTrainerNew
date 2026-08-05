package com.pelvictrainer.data.repository

import com.pelvictrainer.datastore.PelvicDataStore
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

    override fun getTrainingLevel(): Flow<TrainingLevel?> {
        return dataStore.getTrainingLevel()
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return dataStore.isOnboardingCompleted()
    }

    override suspend fun updateTrainingLevel(level: TrainingLevel) {
        dataStore.updateTrainingLevel(level)
    }

    override suspend fun completeOnboarding() {
        dataStore.completeOnboarding()
    }
}