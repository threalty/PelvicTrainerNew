package com.pelvictrainer.feature


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class TrainingSettingsViewModel @Inject constructor(

    private val userPreferencesRepository: UserPreferencesRepository

) : ViewModel() {



    private val presets = defaultPresets()


    private val _state = MutableStateFlow(

        TrainingSettingsState(

            presets = presets,

            selectedPreset = presets.firstOrNull()

        )

    )


    val state: StateFlow<TrainingSettingsState> =
        _state.asStateFlow()



    fun selectPreset(
        preset: TrainingPreset
    ) {

        _state.value =
            _state.value.copy(
                selectedPreset = preset
            )

    }



    fun setTrainingLevel(
        level: TrainingLevel
    ) {

        viewModelScope.launch {

            userPreferencesRepository
                .setTrainingLevel(level)

        }

    }



    fun setNotificationsEnabled(
        enabled: Boolean
    ) {

        viewModelScope.launch {

            userPreferencesRepository
                .setNotificationsEnabled(enabled)

        }

    }



    private fun defaultPresets(): List<TrainingPreset> = listOf(


        TrainingPreset(

            id = "beginner",

            name = "Начальный",

            description = "Лёгкая тренировка",

            contractSeconds = 3,

            holdSeconds = 3,

            relaxSeconds = 5,

            repeats = 10

        ),



        TrainingPreset(

            id = "medium",

            name = "Средний",

            description = "Стандартная тренировка",

            contractSeconds = 5,

            holdSeconds = 5,

            relaxSeconds = 5,

            repeats = 15

        ),



        TrainingPreset(

            id = "advanced",

            name = "Продвинутый",

            description = "Интенсивная тренировка",

            contractSeconds = 8,

            holdSeconds = 8,

            relaxSeconds = 5,

            repeats = 20

        )


    )


}