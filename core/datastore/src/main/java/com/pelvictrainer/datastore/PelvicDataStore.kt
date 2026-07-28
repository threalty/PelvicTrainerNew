package com.pelvictrainer.datastore


import androidx.datastore.core.DataStore

import androidx.datastore.preferences.core.Preferences

import kotlinx.coroutines.flow.Flow



interface PelvicDataStore {


    val userPreferences: Flow<UserPreferences>


    val trainingSettings: Flow<TrainingSettings>



    suspend fun updateOnboardingCompleted(
        completed: Boolean
    )



    suspend fun updateTrainingLevel(
        level: TrainingLevel
    )



    suspend fun updateTrainingSettings(
        settings: TrainingSettings
    )

}