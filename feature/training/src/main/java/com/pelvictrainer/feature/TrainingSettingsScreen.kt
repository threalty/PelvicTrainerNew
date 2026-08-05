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
import com.pelvictrainer.domain.model.DefaultTrainingPresets
import com.pelvictrainer.domain.model.TrainingPreset

private val trainingPresets = listOf(
    DefaultTrainingPresets.beginnerPreset,
    DefaultTrainingPresets.intermediatePreset,
    DefaultTrainingPresets.advancedPreset
)

@Composable
fun TrainingSettingsScreen(
    onStartTraining: (TrainingPreset) -> Unit
) {
    var selectedPreset by remember {
        mutableStateOf(trainingPresets[0])
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Выберите уровень тренировки",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trainingPresets) { preset ->
                Card(
                    modifier = Modifier.clickable {
                        Log.d("TRAINING_SETTINGS", "CLICK preset=$preset")
                        selectedPreset = preset
                    }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        RadioButton(
                            selected = selectedPreset.id == preset.id,
                            onClick = {
                                selectedPreset = preset
                                Log.d("TRAINING_SETTINGS", "RADIO selected=$selectedPreset")
                            }
                        )

                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${preset.squeezeTime} сек сжатие • " +
                                    "${preset.holdTime} сек удержание • " +
                                    "${preset.relaxTime} сек расслабление • " +
                                    "${preset.totalReps} повторов"
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                Log.d("TRAINING_SETTINGS", "BUTTON CLICK selected=$selectedPreset")
                onStartTraining(selectedPreset)
            }
        ) {
            Text(text = "Начать тренировку")
        }
    }
}