package com.pelvictrainer.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



@Composable
fun CompletionScreen(

    completedRepeats: Int,

    totalRepeats: Int,

    durationSeconds: Int,

    onRestart: () -> Unit

) {


    Column(

        verticalArrangement = Arrangement.Center

    ) {



        Text(

            text = "Тренировка завершена"

        )



        Spacer(
            modifier = Modifier.height(24.dp)
        )



        Text(

            text =
                "Повторы: $completedRepeats / $totalRepeats"

        )



        Text(

            text =
                "Время: ${durationSeconds} сек"

        )



        Spacer(
            modifier = Modifier.height(24.dp)
        )



        Button(

            onClick = onRestart

        ) {


            Text(

                text = "Новая тренировка"

            )


        }


    }


}