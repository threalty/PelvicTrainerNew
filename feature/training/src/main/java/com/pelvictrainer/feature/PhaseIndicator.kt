package com.pelvictrainer.feature


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.pelvictrainer.training.TrainingPhase



@Composable
fun PhaseIndicator(

    phase: TrainingPhase,

    modifier: Modifier = Modifier

) {


    val title = when (phase) {

        TrainingPhase.IDLE ->
            "Готово"


        TrainingPhase.CONTRACT ->
            "Сжать"


        TrainingPhase.HOLD ->
            "Удержание"


        TrainingPhase.RELAX ->
            "Расслабление"


        TrainingPhase.COMPLETE ->
            "Завершено"

    }



    Column(

        modifier = modifier.padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {


        Text(

            text = title

        )


    }

}