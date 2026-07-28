package com.pelvictrainer.feature


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.training.TrainingPhase

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



data class TrainingUiState(

    val phase: TrainingPhase = TrainingPhase.IDLE,

    val secondsLeft: Int = 0,

    val currentRepeat: Int = 0,

    val totalRepeats: Int = 0,

    val progress: Float = 0f,

    val isRunning: Boolean = false,

    val completed: Boolean = false

)



class TrainingViewModel : ViewModel() {



    private val _state =
        MutableStateFlow(
            TrainingUiState()
        )


    val state: StateFlow<TrainingUiState> =
        _state.asStateFlow()



    private var timerJob: Job? = null



    private var config = TrainingConfig(

        contractSeconds = 3,

        holdSeconds = 3,

        relaxSeconds = 5,

        repeats = 10

    )





    fun loadPreset(

        presetId: String

    ) {



        config = when(presetId) {



            "beginner" ->

                TrainingConfig(

                    contractSeconds = 3,

                    holdSeconds = 3,

                    relaxSeconds = 5,

                    repeats = 10

                )



            "medium" ->

                TrainingConfig(

                    contractSeconds = 5,

                    holdSeconds = 5,

                    relaxSeconds = 5,

                    repeats = 15

                )



            "advanced" ->

                TrainingConfig(

                    contractSeconds = 8,

                    holdSeconds = 8,

                    relaxSeconds = 5,

                    repeats = 20

                )



            else ->

                TrainingConfig(

                    contractSeconds = 3,

                    holdSeconds = 3,

                    relaxSeconds = 5,

                    repeats = 10

                )


        }



        _state.value =

            _state.value.copy(

                totalRepeats = config.repeats

            )



    }







    fun toggleTraining() {


        if(_state.value.isRunning) {


            stopTraining()


        } else {


            startTraining()


        }


    }







    fun startTraining() {



        timerJob?.cancel()



        timerJob =

            viewModelScope.launch {



                _state.value =

                    _state.value.copy(

                        isRunning = true,

                        completed = false,

                        currentRepeat = 1

                    )





                repeat(config.repeats) {



                    runPhase(

                        TrainingPhase.CONTRACT,

                        config.contractSeconds

                    )



                    runPhase(

                        TrainingPhase.HOLD,

                        config.holdSeconds

                    )



                    runPhase(

                        TrainingPhase.RELAX,

                        config.relaxSeconds

                    )





                    if(
                        _state.value.currentRepeat < config.repeats
                    ) {


                        _state.value =

                            _state.value.copy(

                                currentRepeat =
                                    _state.value.currentRepeat + 1

                            )


                    }



                }






                _state.value =

                    _state.value.copy(

                        phase = TrainingPhase.COMPLETE,

                        secondsLeft = 0,

                        isRunning = false,

                        completed = true,

                        progress = 1f

                    )



            }



    }








    private suspend fun runPhase(

        phase: TrainingPhase,

        seconds: Int

    ) {



        _state.value =

            _state.value.copy(

                phase = phase,

                secondsLeft = seconds,

                progress = 0f

            )




        for(i in seconds downTo 1) {



            _state.value =

                _state.value.copy(

                    secondsLeft = i,

                    progress =
                        1f -
                                (
                                        i.toFloat()
                                                /
                                                seconds.toFloat()
                                        )

                )



            delay(1000)



        }



    }







    private fun stopTraining() {


        timerJob?.cancel()

        timerJob = null



        _state.value =

            TrainingUiState()



    }



}