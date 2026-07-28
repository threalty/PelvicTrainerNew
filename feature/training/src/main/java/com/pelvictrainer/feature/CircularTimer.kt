package com.pelvictrainer.feature


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun CircularTimer(

    seconds: Int,

    progress: Float,

    color: Color,

    modifier: Modifier = Modifier

) {


    val animatedProgress =
        animateFloatAsState(
            targetValue = progress,
            label = "timer_progress"
        )


    Box(

        modifier = modifier.size(220.dp),

        contentAlignment = Alignment.Center

    ) {


        Canvas(

            modifier = Modifier.matchParentSize()

        ) {


            drawArc(

                color = Color.LightGray,

                startAngle = -90f,

                sweepAngle = 360f,

                useCenter = false,

                style = Stroke(
                    width = 22f,
                    cap = StrokeCap.Round
                )

            )


            drawArc(

                color = color,

                startAngle = -90f,

                sweepAngle =
                    360f * animatedProgress.value,

                useCenter = false,

                style = Stroke(
                    width = 22f,
                    cap = StrokeCap.Round
                )

            )


        }



        Text(

            text = seconds.toString(),

            fontSize = 56.sp

        )


    }


}