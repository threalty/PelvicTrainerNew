package com.pelvictrainer.domain.usecase

import com.pelvictrainer.domain.repository.TrainingRepository
import javax.inject.Inject


class StartTrainingUseCase @Inject constructor(
    private val repository: TrainingRepository
) {

    suspend operator fun invoke() {

        repository.startSession()

    }

}