package com.pelvictrainer.feature


import android.util.Log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel

import com.pelvictrainer.domain.model.TrainingPreset



@Composable
fun TrainingSettingsScreen(

    onStartTraining: (TrainingPreset) -> Unit,

    viewModel: TrainingSettingsViewModel = hiltViewModel()

) {


    val state by viewModel.state.collectAsState()



    Log.d(

        "TRAINING_SETTINGS",

        "selected=${state.selectedPreset}"

    )



    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Top

    ) {



        Text(

            text = "Выберите тренировку",

            style = MaterialTheme.typography.headlineMedium

        )



        Spacer(

            modifier = Modifier.height(20.dp)

        )



        state.presets.forEach { preset ->



            Card(

                modifier = Modifier

                    .fillMaxWidth()

                    .padding(vertical = 8.dp)

                    .clickable {


                        Log.d(

                            "TRAINING_SETTINGS",

                            "CLICK preset=$preset"

                        )


                        viewModel.selectPreset(preset)


                    }

            ) {



                Column(

                    modifier = Modifier.padding(16.dp)

                ) {



                    Text(

                        text = preset.name,

                        style = MaterialTheme.typography.titleLarge

                    )


                    Spacer(

                        modifier = Modifier.height(8.dp)

                    )


                    Text(

                        text = preset.description

                    )


                }


            }


        }




        Spacer(

            modifier = Modifier.height(24.dp)

        )



        Button(

            enabled = state.selectedPreset != null,

            onClick = {


                Log.d(

                    "TRAINING_SETTINGS",

                    "BUTTON CLICK selected=${state.selectedPreset}"

                )


                state.selectedPreset?.let {


                    onStartTraining(it)


                }


            }

        ) {



            Text(

                text = "Начать тренировку"

            )


        }


    }


}