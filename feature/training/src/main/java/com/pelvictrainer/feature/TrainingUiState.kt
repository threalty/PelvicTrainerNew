package com.pelvictrainer.feature.training

import com.pelvictrainer.training.TrainingPhase

data class TrainingUiState(

    val phase: TrainingPhase = TrainingPhase.IDLE,

    val secondsLeft: Int = 0,

    val currentRepeat: Int = 1,

    val totalRepeats: Int = 10,

    val progress: Float = 0f,

    val isRunning: Boolean = false,

    val completed: Boolean = false

)