package com.pelvictrainer.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pelvictrainer.domain.model.TrainingLevel

@Composable
fun LevelSelectionScreen(
    onLevelSelected: (TrainingLevel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Выберите уровень",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Выберите уровень сложности тренировок",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        LevelCard(
            level = TrainingLevel.BEGINNER,
            title = "Новичок",
            description = "Для тех, кто только начинает",
            onClick = { onLevelSelected(TrainingLevel.BEGINNER) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LevelCard(
            level = TrainingLevel.INTERMEDIATE,
            title = "Любитель",
            description = "Для тех, кто уже знаком с упражнениями",
            onClick = { onLevelSelected(TrainingLevel.INTERMEDIATE) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LevelCard(
            level = TrainingLevel.ADVANCED,
            title = "Профи",
            description = "Для опытных пользователей",
            onClick = { onLevelSelected(TrainingLevel.ADVANCED) }
        )
    }
}

@Composable
private fun LevelCard(
    level: TrainingLevel,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}