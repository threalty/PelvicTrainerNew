package com.pelvictrainer.feature.training

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CircularTimer(

    seconds: Int,

    progress: Float

) {

    Box(
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator(

            progress = { progress },

            modifier = Modifier.size(220.dp),

            strokeWidth = 12.dp

        )

        Text(

            text = "%02d".format(seconds),

            style = MaterialTheme.typography.displayMedium

        )

    }

}