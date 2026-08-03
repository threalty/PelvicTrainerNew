package com.pelvictrainer.domain.usecase

import com.pelvictrainer.domain.repository.TrainingRepository

class CompleteTrainingUseCase(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(presetId: Long) {
        repository.completeTraining(presetId)
    }
}