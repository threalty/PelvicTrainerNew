package com.pelvictrainer.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingState())
    val uiState: StateFlow<OnboardingState> = _uiState.asStateFlow()

    init {
        checkStatus()
    }

    private fun checkStatus() {
        viewModelScope.launch {
            // Проверяем, не завершен ли уже онбординг
            val completed = userPreferencesRepository.isOnboardingCompleted().first()
            if (completed) {
                _uiState.value = _uiState.value.copy(isCompleted = true)
                return@launch
            }

            // Загружаем текущий уровень, если есть
            val level = userPreferencesRepository.getTrainingLevel().first()
            if (level != null) {
                _uiState.value = _uiState.value.copy(selectedLevel = level)
            }
        }
    }

    fun selectLevel(level: TrainingLevel) {
        _uiState.value = _uiState.value.copy(selectedLevel = level)
    }

    fun finishOnboarding() {
        viewModelScope.launch {
            val level = _uiState.value.selectedLevel
            if (level != null) {
                userPreferencesRepository.updateTrainingLevel(level)
                userPreferencesRepository.completeOnboarding()
                _uiState.value = _uiState.value.copy(isCompleted = true)
            }
        }
    }
}