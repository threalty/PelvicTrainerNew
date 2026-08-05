package com.pelvictrainer.feature.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.domain.model.TrainingLevel

@OptIn(ExperimentalMaterial3Api::class) // <--- Добавьте эту строку
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isCompleted) {
        LaunchedEffect(Unit) {
            onFinish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Настройка профиля") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Выберите уровень сложности")

                Spacer(modifier = Modifier.height(16.dp))

                TrainingLevel.values().forEach { level ->
                    FilterChip(
                        selected = state.selectedLevel == level,
                        onClick = { viewModel.selectLevel(level) },
                        label = { Text(level.name) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.finishOnboarding() },
                    enabled = state.selectedLevel != null
                ) {
                    Text("Готово")
                }
            }
        }
    }
}