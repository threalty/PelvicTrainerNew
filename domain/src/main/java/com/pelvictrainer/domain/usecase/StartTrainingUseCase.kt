package com.pelvictrainer.domain.usecase

import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.domain.repository.TrainingRepository

class StartTrainingUseCase(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(preset: TrainingPreset) {
        repository.startTraining(preset)
    }
}