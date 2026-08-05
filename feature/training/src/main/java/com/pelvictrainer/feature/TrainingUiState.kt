package com.pelvictrainer.feature

import com.pelvictrainer.domain.model.TrainingPhase
import com.pelvictrainer.domain.model.TrainingPreset

sealed class TrainingUiState {
    object Loading : TrainingUiState()
    data class Ready(val preset: TrainingPreset) : TrainingUiState()
    data class Training(
        val preset: TrainingPreset,
        val phase: TrainingPhase,
        val progress: Float,
        val timeLeft: Int,
        val repsLeft: Int,
        val currentRep: Int
    ) : TrainingUiState()
    data class Finished(val preset: TrainingPreset) : TrainingUiState()
    data class Error(val message: String) : TrainingUiState()
}