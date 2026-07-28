package com.pelvictrainer.data.repository

import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.UserPreferences
import com.pelvictrainer.domain.repository.UserPreferencesRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(

) : UserPreferencesRepository {


    private val _userPreferences =
        MutableStateFlow(
            UserPreferences()
        )


    override val userPreferences: Flow<UserPreferences>
        get() = _userPreferences.asStateFlow()



    override suspend fun setTrainingLevel(
        level: TrainingLevel
    ) {

        _userPreferences.value =
            _userPreferences.value.copy(
                trainingLevel = level
            )

    }



    override suspend fun setNotificationsEnabled(
        enabled: Boolean
    ) {

        _userPreferences.value =
            _userPreferences.value.copy(
                notificationsEnabled = enabled
            )

    }


}