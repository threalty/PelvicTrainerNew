package com.pelvictrainer.domain.usecase

import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.repository.TrainingRepository
import javax.inject.Inject


class CompleteTrainingUseCase @Inject constructor(
    private val repository: TrainingRepository
) {


    suspend operator fun invoke(
        session: TrainingSession
    ) {

        repository.completeSession(
            session
        )

    }

}