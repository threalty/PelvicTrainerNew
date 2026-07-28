package com.pelvictrainer.feature


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel



@Composable
fun TrainingScreen(

    presetId: String,

    viewModel: TrainingViewModel = hiltViewModel()

) {



    val state by viewModel.state.collectAsState()



    LaunchedEffect(presetId) {


        viewModel.loadPreset(

            presetId

        )


        viewModel.startTraining()


    }





    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),


        verticalArrangement = Arrangement.Center


    ) {



        Text(

            text = state.phase.name,

            style = MaterialTheme.typography.headlineLarge

        )



        Spacer(

            modifier = Modifier.height(20.dp)

        )



        Text(

            text = "Осталось: ${state.secondsLeft}"

        )



        Spacer(

            modifier = Modifier.height(20.dp)

        )



        Text(

            text =
                "Повтор ${state.currentRepeat}/${state.totalRepeats}"

        )



        Spacer(

            modifier = Modifier.height(30.dp)

        )



        Button(

            onClick = {

                viewModel.toggleTraining()

            }

        ) {



            Text(

                if(state.isRunning)

                    "Остановить"

                else

                    "Начать"

            )


        }


    }



}