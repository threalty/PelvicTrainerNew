package com.pelvictrainer.datastore


import androidx.datastore.core.DataStore

import androidx.datastore.preferences.core.Preferences

import androidx.datastore.preferences.core.booleanPreferencesKey

import androidx.datastore.preferences.core.intPreferencesKey

import androidx.datastore.preferences.core.stringPreferencesKey

import androidx.datastore.preferences.core.edit


import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.map


import javax.inject.Inject



class PreferencesDataStore @Inject constructor(

    private val dataStore: DataStore<Preferences>

) : PelvicDataStore {



    companion object {


        private val ONBOARDING_KEY =
            booleanPreferencesKey(
                "onboarding_completed"
            )


        private val AGE_KEY =
            intPreferencesKey(
                "user_age"
            )


        private val LEVEL_KEY =
            stringPreferencesKey(
                "training_level"
            )


        private val GOAL_KEY =
            intPreferencesKey(
                "daily_goal_minutes"
            )


        private val REMINDER_ENABLED_KEY =
            booleanPreferencesKey(
                "reminder_enabled"
            )


        private val REMINDER_HOUR_KEY =
            intPreferencesKey(
                "reminder_hour"
            )


        private val REMINDER_MINUTE_KEY =
            intPreferencesKey(
                "reminder_minute"
            )

    }



    override val userPreferences: Flow<UserPreferences> =

        dataStore.data.map { preferences ->


            UserPreferences(

                isOnboardingCompleted =
                    preferences[ONBOARDING_KEY] ?: false,


                userAge =
                    preferences[AGE_KEY],


                trainingLevel =
                    TrainingLevel.valueOf(

                        preferences[LEVEL_KEY]
                            ?: TrainingLevel.BEGINNER.name

                    )

            )

        }



    override val trainingSettings: Flow<TrainingSettings> =

        dataStore.data.map { preferences ->


            TrainingSettings(

                dailyGoalMinutes =
                    preferences[GOAL_KEY] ?: 10,


                reminderEnabled =
                    preferences[REMINDER_ENABLED_KEY]
                        ?: false,


                reminderHour =
                    preferences[REMINDER_HOUR_KEY]
                        ?: 20,


                reminderMinute =
                    preferences[REMINDER_MINUTE_KEY]
                        ?: 0

            )

        }



    override suspend fun updateOnboardingCompleted(
        completed: Boolean
    ) {


        dataStore.edit { preferences ->


            preferences[ONBOARDING_KEY] =
                completed


        }

    }



    override suspend fun updateTrainingLevel(
        level: TrainingLevel
    ) {


        dataStore.edit { preferences ->


            preferences[LEVEL_KEY] =
                level.name


        }

    }



    override suspend fun updateTrainingSettings(
        settings: TrainingSettings
    ) {


        dataStore.edit { preferences ->


            preferences[GOAL_KEY] =
                settings.dailyGoalMinutes


            preferences[REMINDER_ENABLED_KEY] =
                settings.reminderEnabled


            preferences[REMINDER_HOUR_KEY] =
                settings.reminderHour


            preferences[REMINDER_MINUTE_KEY] =
                settings.reminderMinute


        }

    }

}