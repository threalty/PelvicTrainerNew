package com.pelvictrainer.feature


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.TrainingPreset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class TrainingViewModel @Inject constructor(

) : ViewModel() {



    private val presets = listOf(

        TrainingPreset(
            id = "beginner",
            name = "Начальный",
            description = "Лёгкая тренировка",
            contractSeconds = 3,
            holdSeconds = 3,
            relaxSeconds = 5,
            repeats = 10
        ),


        TrainingPreset(
            id = "medium",
            name = "Средний",
            description = "Стандартная тренировка",
            contractSeconds = 5,
            holdSeconds = 5,
            relaxSeconds = 5,
            repeats = 15
        ),


        TrainingPreset(
            id = "advanced",
            name = "Продвинутый",
            description = "Интенсивная тренировка",
            contractSeconds = 8,
            holdSeconds = 8,
            relaxSeconds = 5,
            repeats = 20
        )

    )



    private var preset: TrainingPreset? = null


    private var timerJob: Job? = null




    private val _state = MutableStateFlow(

        TrainingUiState()

    )



    val state: StateFlow<TrainingUiState> =

        _state.asStateFlow()





    fun loadPreset(id: String) {


        preset = presets.firstOrNull {

            it.id == id

        }



        val p = preset ?: return





        _state.value = TrainingUiState(

            phase = TrainingPhase.CONTRACT,

            secondsLeft = p.contractSeconds,

            phaseDuration = p.contractSeconds,

            currentRepeat = 1,

            totalRepeats = p.repeats,

            isRunning = false,

            completed = false

        )


    }







    fun start() {


        if (_state.value.isRunning) return



        _state.value = _state.value.copy(

            isRunning = true

        )




        timerJob = viewModelScope.launch {



            while(

                _state.value.isRunning &&

                !_state.value.completed

            ) {



                delay(1000)



                tick()

            }


        }


    }







    fun pause() {


        _state.value = _state.value.copy(

            isRunning = false

        )



        timerJob?.cancel()


    }







    fun stop() {


        timerJob?.cancel()



        _state.value = _state.value.copy(

            isRunning = false,

            completed = true,

            phase = TrainingPhase.COMPLETE,

            secondsLeft = 0

        )


    }








    private fun tick() {


        val current = _state.value




        if(current.secondsLeft > 1) {


            _state.value = current.copy(

                secondsLeft = current.secondsLeft - 1

            )



        } else {


            nextPhase()

        }


    }









    private fun nextPhase() {


        val p = preset ?: return


        val current = _state.value





        when(current.phase) {



            TrainingPhase.CONTRACT -> {



                _state.value = current.copy(

                    phase = TrainingPhase.HOLD,

                    secondsLeft = p.holdSeconds,

                    phaseDuration = p.holdSeconds

                )


            }





            TrainingPhase.HOLD -> {



                _state.value = current.copy(

                    phase = TrainingPhase.RELAX,

                    secondsLeft = p.relaxSeconds,

                    phaseDuration = p.relaxSeconds

                )


            }





            TrainingPhase.RELAX -> {



                if(current.currentRepeat >= p.repeats) {



                    _state.value = current.copy(

                        phase = TrainingPhase.COMPLETE,

                        secondsLeft = 0,

                        completed = true,

                        isRunning = false,

                        phaseDuration = 0

                    )



                } else {



                    _state.value = current.copy(

                        phase = TrainingPhase.CONTRACT,

                        secondsLeft = p.contractSeconds,

                        phaseDuration = p.contractSeconds,

                        currentRepeat = current.currentRepeat + 1

                    )


                }


            }





            TrainingPhase.COMPLETE -> Unit


        }


    }

}