package com.pelvictrainer.feature.training

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.pelvictrainer.training.TrainingPhase

@Composable
fun PhaseText(
    phase: TrainingPhase
) {

    val text = when (phase) {

        TrainingPhase.IDLE -> "ГОТОВ"

        TrainingPhase.CONTRACT -> "СЖАТЬ"

        TrainingPhase.HOLD -> "УДЕРЖИВАТЬ"

        TrainingPhase.RELAX -> "РАССЛАБИТЬ"

        TrainingPhase.COMPLETE -> "ГОТОВО"

    }

    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium
    )

}