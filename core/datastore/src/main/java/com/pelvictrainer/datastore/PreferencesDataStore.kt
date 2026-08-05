package com.pelvictrainer.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

    override fun getUserPreferences(): Flow<UserPreferences> {
        return context.dataStore.data.map { preferences ->
            UserPreferences(
                isOnboardingCompleted = preferences[ONBOARDING_COMPLETED_KEY] ?: false,
                trainingLevel = preferences[TRAINING_LEVEL_KEY]?.let {
                    TrainingLevel.valueOf(it)
                } ?: TrainingLevel.BEGINNER,
                userAge = preferences[USER_AGE_KEY]
            )
        }
    }

    override fun getTrainingLevel(): Flow<TrainingLevel?> {
        return context.dataStore.data.map { preferences ->
            preferences[TRAINING_LEVEL_KEY]?.let {
                try {
                    TrainingLevel.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] ?: false
        }
    }

    override suspend fun updateTrainingLevel(level: TrainingLevel) {
        context.dataStore.edit { preferences ->
            preferences[TRAINING_LEVEL_KEY] = level.name
        }
    }

    override suspend fun completeOnboarding() {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = true
        }
    }
}