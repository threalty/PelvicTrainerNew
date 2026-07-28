package com.pelvictrainer.training


sealed interface TrainingEvent {


    data object StartExercise : TrainingEvent


    data object CompleteExercise : TrainingEvent


    data object Reset : TrainingEvent

}