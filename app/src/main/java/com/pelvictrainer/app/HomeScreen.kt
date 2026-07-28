package com.pelvictrainer.app


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



@Composable
fun HomeScreen(

    onStart: () -> Unit,

    onSettings: () -> Unit

) {


    Column(

        modifier =
            Modifier
                .fillMaxSize(),

        verticalArrangement =
            Arrangement.Center,

        horizontalAlignment =
            Alignment.CenterHorizontally

    ) {


        Text(
            text = "Pelvic Trainer",
            style = MaterialTheme.typography.headlineMedium
        )


        Spacer(
            modifier =
                Modifier.height(40.dp)
        )



        Button(

            onClick = onStart

        ) {

            Text(
                "Начать тренировку"
            )

        }



        Spacer(
            modifier =
                Modifier.height(20.dp)
        )



        OutlinedButton(

            onClick = onSettings

        ) {

            Text(
                "Настройки"
            )

        }


    }

}