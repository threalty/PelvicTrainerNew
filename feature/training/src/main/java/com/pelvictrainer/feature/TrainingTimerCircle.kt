package com.pelvictrainer.feature


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp


@Composable
fun TrainingTimerCircle(

    progress: Float,

    phaseColor: Color

) {


    Canvas(

        modifier =
            Modifier
                .size(260.dp)

    ) {


        val strokeWidth = 28f



        drawCircle(

            color = Color.LightGray,

            style =
                androidx.compose.ui.graphics.drawscope
                    .Stroke(
                        width = strokeWidth
                    )

        )



        drawArc(

            color = phaseColor,

            startAngle = -90f,

            sweepAngle =
                360f * progress,

            useCenter = false,

            style =
                androidx.compose.ui.graphics.drawscope
                    .Stroke(

                        width = strokeWidth,

                        cap = StrokeCap.Round

                    ),

            size = size,

            topLeft = Offset.Zero

        )

    }


}