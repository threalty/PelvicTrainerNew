package com.pelvictrainer.feature.onboarding


import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope


import com.pelvictrainer.datastore.TrainingLevel

import com.pelvictrainer.domain.repository.UserPreferencesRepository


import dagger.hilt.android.lifecycle.HiltViewModel


import kotlinx.coroutines.flow.MutableStateFlow

import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch


import javax.inject.Inject



@HiltViewModel

class OnboardingViewModel @Inject constructor(

    private val repository: UserPreferencesRepository

) : ViewModel() {



    private val _state =

        MutableStateFlow(
            OnboardingState()
        )


    val state: StateFlow<OnboardingState> =

        _state.asStateFlow()



    fun selectLevel(

        level: TrainingLevel

    ) {


        _state.value =

            _state.value.copy(

                selectedLevel = level

            )

    }



    fun complete() {


        viewModelScope.launch {


            _state.value =

                _state.value.copy(

                    loading = true

                )



            repository.updateTrainingLevel(

                _state.value.selectedLevel

            )



            repository.completeOnboarding()



            _state.value =

                _state.value.copy(

                    loading = false,

                    completed = true

                )

        }

    }

}