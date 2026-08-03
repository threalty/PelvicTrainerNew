package com.pelvictrainer.feature


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle



@Composable
fun TrainingScreen(

    presetId: String,

    viewModel: TrainingViewModel = hiltViewModel()

) {



    val state by viewModel.state.collectAsStateWithLifecycle()



    val scale = remember {

        Animatable(1f)

    }





    LaunchedEffect(presetId) {


        viewModel.loadPreset(

            presetId

        )


    }





    LaunchedEffect(state.phase) {


        when(state.phase) {



            TrainingPhase.CONTRACT -> {


                scale.animateTo(

                    targetValue = 1.2f,

                    animationSpec = tween(400)

                )


            }



            TrainingPhase.HOLD -> {


                scale.animateTo(

                    targetValue = 1.25f,

                    animationSpec = tween(300)

                )


            }




            TrainingPhase.RELAX -> {


                scale.animateTo(

                    targetValue = 0.85f,

                    animationSpec = tween(600)

                )


            }





            TrainingPhase.COMPLETE -> {


                scale.animateTo(

                    targetValue = 1f,

                    animationSpec = tween(500)

                )


            }


        }


    }







    val phaseColor = when(state.phase) {


        TrainingPhase.CONTRACT ->

            MaterialTheme.colorScheme.primary



        TrainingPhase.HOLD ->

            MaterialTheme.colorScheme.secondary



        TrainingPhase.RELAX ->

            MaterialTheme.colorScheme.tertiary



        TrainingPhase.COMPLETE ->

            MaterialTheme.colorScheme.primaryContainer


    }





    val phaseText = when(state.phase) {


        TrainingPhase.CONTRACT ->

            "Сжать"



        TrainingPhase.HOLD ->

            "Держать"



        TrainingPhase.RELAX ->

            "Расслабить"



        TrainingPhase.COMPLETE ->

            "Готово"


    }






    Box(

        modifier = Modifier

            .fillMaxSize()

            .background(

                MaterialTheme.colorScheme.background

            ),

        contentAlignment = Alignment.Center

    ) {



        Column(

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center

        ) {



            Text(

                text = phaseText,

                style = MaterialTheme.typography.headlineLarge

            )





            Spacer(

                modifier = Modifier.height(40.dp)

            )







            Box(

                modifier = Modifier

                    .size(240.dp)

                    .scale(scale.value),

                contentAlignment = Alignment.Center

            ) {



                Canvas(

                    modifier = Modifier.fillMaxSize()

                ) {



                    val strokeWidth = 28.dp.toPx()



                    drawCircle(

                        color = MaterialTheme.colorScheme.surfaceVariant,

                        style = Stroke(

                            width = strokeWidth

                        )

                    )




                    val progress =

                        if(state.phaseDuration > 0)

                            state.secondsLeft.toFloat() /

                                    state.phaseDuration.toFloat()

                        else

                            0f





                    drawArc(

                        color = phaseColor,

                        startAngle = -90f,

                        sweepAngle =

                            360f * progress.coerceIn(

                                0f,

                                1f

                            ),

                        useCenter = false,

                        style = Stroke(

                            width = strokeWidth,

                            cap = StrokeCap.Round

                        )

                    )


                }






                Text(

                    text = state.secondsLeft.toString(),

                    style = MaterialTheme.typography.displayLarge

                )


            }







            Spacer(

                modifier = Modifier.height(35.dp)

            )







            Text(

                text =

                    "${state.currentRepeat} / ${state.totalRepeats}",

                style = MaterialTheme.typography.titleMedium

            )






            Spacer(

                modifier = Modifier.height(30.dp)

            )






            if(!state.completed) {



                Button(

                    onClick = {


                        if(state.isRunning) {


                            viewModel.pause()


                        } else {


                            viewModel.start()


                        }


                    }

                ) {



                    Text(

                        text =

                            if(state.isRunning)

                                "Пауза"

                            else

                                "Старт"

                    )


                }



            } else {



                Text(

                    text = "Тренировка завершена",

                    style = MaterialTheme.typography.headlineSmall

                )


            }



        }


    }


}