package com.pelvictrainer.feature


import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pelvictrainer.domain.model.TrainingPreset



private val trainingPresets = listOf(

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



@Composable
fun TrainingSettingsScreen(

    onStartTraining: (TrainingPreset) -> Unit

) {


    var selectedPreset by remember {

        mutableStateOf(

            trainingPresets[1]

        )

    }



    Column(

        modifier = Modifier

            .fillMaxSize()

            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {



        Text(

            text = "Настройка тренировки",

            style = MaterialTheme.typography.headlineMedium

        )



        Spacer(

            modifier = Modifier.height(30.dp)

        )





        LazyColumn(

            verticalArrangement = Arrangement.spacedBy(12.dp)

        ) {


            items(trainingPresets) { preset ->



                Card(

                    modifier = Modifier

                        .clickable {


                            Log.d(

                                "TRAINING_SETTINGS",

                                "CLICK preset=$preset"

                            )


                            selectedPreset = preset


                            Log.d(

                                "TRAINING_SETTINGS",

                                "selected=$selectedPreset"

                            )


                        }

                ) {



                    Column(

                        modifier = Modifier.padding(16.dp)

                    ) {



                        RadioButton(

                            selected =

                                selectedPreset.id == preset.id,

                            onClick = {


                                selectedPreset = preset


                                Log.d(

                                    "TRAINING_SETTINGS",

                                    "RADIO selected=$selectedPreset"

                                )


                            }

                        )





                        Text(

                            text = preset.name,

                            style = MaterialTheme.typography.titleLarge

                        )





                        Text(

                            text = preset.description

                        )





                        Spacer(

                            modifier = Modifier.height(6.dp)

                        )





                        Text(

                            text =

                                "${preset.contractSeconds} сек сжатие • " +

                                        "${preset.holdSeconds} сек удержание • " +

                                        "${preset.relaxSeconds} сек отдых • " +

                                        "${preset.repeats} повторов"

                        )


                    }


                }


            }


        }





        Spacer(

            modifier = Modifier.height(30.dp)

        )





        Button(

            onClick = {


                Log.d(

                    "TRAINING_SETTINGS",

                    "BUTTON CLICK selected=$selectedPreset"

                )


                onStartTraining(

                    selectedPreset

                )


            }

        ) {


            Text(

                text = "Начать тренировку"

            )


        }


    }


}