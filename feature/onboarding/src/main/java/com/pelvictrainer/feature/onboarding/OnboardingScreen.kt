package com.pelvictrainer.feature.onboarding


import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.height


import androidx.compose.material3.Button

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.RadioButton

import androidx.compose.material3.Text


import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue


import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp


import androidx.hilt.navigation.compose.hiltViewModel


import com.pelvictrainer.datastore.TrainingLevel



@Composable

fun OnboardingScreen(

    viewModel: OnboardingViewModel = hiltViewModel(),

    onCompleted: () -> Unit

) {


    val state by viewModel.state.collectAsState()



    Column(

        modifier = Modifier.fillMaxSize(),

        verticalArrangement = Arrangement.Center,

        horizontalAlignment = Alignment.CenterHorizontally

    ) {



        Text(

            text = "PelvicTrainer",

            style = MaterialTheme.typography.headlineLarge

        )



        Spacer(

            modifier = Modifier.height(32.dp)

        )



        Text(

            text = "Выберите уровень"

        )



        TrainingLevel.values().forEach { level ->



            Column {



                RadioButton(

                    selected =

                        state.selectedLevel == level,


                    onClick = {

                        viewModel.selectLevel(
                            level
                        )

                    }

                )


                Text(

                    text = level.name

                )


            }

        }



        Spacer(

            modifier = Modifier.height(24.dp)

        )



        Button(

            onClick = {


                viewModel.complete()



                onCompleted()

            }

        ) {


            Text(

                text = "Начать"

            )


        }


    }

}