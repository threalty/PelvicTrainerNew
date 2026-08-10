package com.pelvictrainer.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutsUiState(
    val presets: List<TrainingPreset> = emptyList(),
    val userLevel: TrainingLevel = TrainingLevel.BEGINNER,
    val isLoading: Boolean = true
)

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutsUiState())
    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            val presets = trainingRepository.getPresets().first()
            val userPrefs = userPreferencesRepository.userPreferences.first()

            _uiState.value = WorkoutsUiState(
                presets = presets,
                userLevel = userPrefs.trainingLevel,
                isLoading = false
            )
        }
    }
}