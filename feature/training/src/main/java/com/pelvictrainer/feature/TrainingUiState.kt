package com.pelvictrainer.feature



data class TrainingUiState(

    val phase: TrainingPhase = TrainingPhase.CONTRACT,

    val secondsLeft: Int = 0,

    val phaseDuration: Int = 0,

    val currentRepeat: Int = 0,

    val totalRepeats: Int = 0,

    val isRunning: Boolean = false,

    val completed: Boolean = false

)



enum class TrainingPhase {


    CONTRACT,


    HOLD,


    RELAX,


    COMPLETE

}