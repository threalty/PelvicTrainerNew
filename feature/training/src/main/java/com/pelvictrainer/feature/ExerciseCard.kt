package com.pelvictrainer.feature


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pelvictrainer.domain.model.Exercise


@Composable
fun ExerciseCard(
    exercise: Exercise,
    seconds: Int
) {

    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "Тренировка мышц тазового дна",
                style = MaterialTheme.typography.titleMedium
            )


            Text(
                text =
                    "Сжатие: ${exercise.contractSeconds} сек\n" +
                            "Удержание: ${exercise.holdSeconds} сек\n" +
                            "Отдых: ${exercise.relaxSeconds} сек"
            )


            Text(
                text = "Текущая фаза: $seconds сек",
                style = MaterialTheme.typography.bodyLarge
            )

        }
    }
}