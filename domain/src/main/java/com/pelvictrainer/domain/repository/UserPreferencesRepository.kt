package com.pelvictrainer.domain.repository


import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow


interface UserPreferencesRepository {


    val userPreferences: Flow<UserPreferences>


    suspend fun setTrainingLevel(
        level: TrainingLevel
    )


    suspend fun setNotificationsEnabled(
        enabled: Boolean
    )

}