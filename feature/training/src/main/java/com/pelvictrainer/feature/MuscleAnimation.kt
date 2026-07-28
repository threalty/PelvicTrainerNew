package com.pelvictrainer.feature


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size

import com.pelvictrainer.training.TrainingPhase



@Composable
fun MuscleAnimation(

    phase: TrainingPhase

) {


    val scaleTarget = when(phase) {


        TrainingPhase.CONTRACT ->
            1.25f


        TrainingPhase.HOLD ->
            1.25f


        TrainingPhase.RELAX ->
            0.85f


        TrainingPhase.COMPLETE ->
            1f


        else ->
            1f

    }



    val scale = animateFloatAsState(

        targetValue = scaleTarget,

        animationSpec =
            tween(
                durationMillis = 700
            ),

        label = "muscle_animation"

    )



    Canvas(

        modifier =
            Modifier
                .size(120.dp)

    ) {


        drawCircle(

            color =
                Color.Red.copy(
                    alpha = 0.65f
                ),


            radius =
                size.minDimension / 2 *
                        scale.value,


            center =
                Offset(
                    size.width / 2,
                    size.height / 2
                )

        )


    }


}