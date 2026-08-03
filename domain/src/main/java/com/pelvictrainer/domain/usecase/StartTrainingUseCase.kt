package com.pelvictrainer.domain.usecase

import com.pelvictrainer.domain.repository.TrainingRepository

class StartTrainingUseCase(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke() {
        repository.startSession()
    }
}