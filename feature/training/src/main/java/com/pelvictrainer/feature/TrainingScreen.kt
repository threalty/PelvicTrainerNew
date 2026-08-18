package com.pelvictrainer.feature

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.domain.model.TrainingPhase
import com.pelvictrainer.domain.model.TrainingPreset
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    presetId: Long,
    onNavigateBack: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel(),
    onNavigateToPremium: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var showDailyLimitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(presetId) {
        viewModel.loadPreset(presetId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Тренировка") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is TrainingUiState.Loading -> CircularProgressIndicator()

                is TrainingUiState.Ready -> TrainingContent(
                    preset = state.preset,
                    phase = TrainingPhase.IDLE,
                    progress = 0f,
                    timeLeft = 0,
                    repsLeft = state.preset.totalReps,
                    onStart = {
                        scope.launch {
                            val canStart = viewModel.canStartTraining()
                            if (canStart) {
                                viewModel.startTraining(state.preset)
                            } else {
                                showDailyLimitDialog = true
                            }
                        }
                    }
                )

                is TrainingUiState.Training -> TrainingContent(
                    preset = state.preset,
                    phase = state.phase,
                    progress = state.progress,
                    timeLeft = state.timeLeft,
                    repsLeft = state.repsLeft,
                    onStart = {}
                )

                is TrainingUiState.Finished -> TrainingFinishedContent(
                    preset = state.preset,
                    onRestart = {
                        scope.launch {
                            val canStart = viewModel.canStartTraining()
                            if (canStart) {
                                viewModel.startTraining(state.preset)
                            } else {
                                showDailyLimitDialog = true
                            }
                        }
                    },
                    onBack = onNavigateBack
                )

                is TrainingUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка: ${state.message}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) { Text("Назад") }
                    }
                }
            }
        }
    }

    if (showDailyLimitDialog) {
        AlertDialog(
            onDismissRequest = { showDailyLimitDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Дневной лимит") },
            text = {
                Text("В бесплатной версии доступна 1 тренировка в сутки. Сегодня вы уже тренировались.\n\nОформите Premium для неограниченного количества тренировок.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDailyLimitDialog = false
                        onNavigateToPremium()
                    }
                ) {
                    Text("Оформить Premium")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDailyLimitDialog = false }) {
                    Text("ОК")
                }
            }
        )
    }
}

@Composable
private fun TrainingContent(
    preset: TrainingPreset,
    phase: TrainingPhase,
    progress: Float,
    timeLeft: Int,
    repsLeft: Int,
    onStart: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = preset.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        TrainingRing(phase = phase, progress = progress)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = getInstructionText(phase, timeLeft),
            style = MaterialTheme.typography.titleLarge,
            color = phaseColor(phase)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Повторений осталось: $repsLeft",
            style = MaterialTheme.typography.bodyLarge
        )

        if (phase == TrainingPhase.IDLE) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onStart) {
                Text("Начать тренировку")
            }
        }
    }
}

@Composable
private fun TrainingRing(phase: TrainingPhase, progress: Float) {
    val strokeWidth = 28.dp
    val size = 240.dp

    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    val activeColor = when (phase) {
        TrainingPhase.SQUEEZE -> MaterialTheme.colorScheme.primary
        TrainingPhase.HOLD -> MaterialTheme.colorScheme.secondary
        TrainingPhase.RELAX -> MaterialTheme.colorScheme.tertiary
        TrainingPhase.FINISHED -> MaterialTheme.colorScheme.primary
        TrainingPhase.IDLE -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val scale by animateFloatAsState(
        targetValue = if (phase == TrainingPhase.HOLD) 1.1f else 1f,
        animationSpec = spring(),
        label = "scale"
    )

    Canvas(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        val diameter = size.toPx()
        val radius = diameter / 2
        val center = Offset(radius, radius)
        val strokeWidthPx = strokeWidth.toPx()

        drawCircle(
            color = surfaceVariantColor,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidthPx)
        )

        val sweepAngle = when (phase) {
            TrainingPhase.SQUEEZE -> 360f * progress
            TrainingPhase.HOLD -> 360f
            TrainingPhase.RELAX -> 360f * (1f - progress)
            TrainingPhase.FINISHED -> 360f
            TrainingPhase.IDLE -> 0f
        }

        if (sweepAngle > 0) {
            drawArc(
                color = activeColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun TrainingFinishedContent(
    preset: TrainingPreset,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Тренировка завершена!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Пресет: ${preset.name}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onBack) {
                Text("Готово")
            }
            Button(onClick = onRestart) {
                Text("Повторить")
            }
        }
    }
}

private fun getInstructionText(phase: TrainingPhase, timeLeft: Int): String {
    return when (phase) {
        TrainingPhase.IDLE -> "Нажмите кнопку чтобы начать"
        TrainingPhase.SQUEEZE -> "Сжимайте! ($timeLeft сек)"
        TrainingPhase.HOLD -> "Держите! ($timeLeft сек)"
        TrainingPhase.RELAX -> "Расслабьтесь ($timeLeft сек)"
        TrainingPhase.FINISHED -> "Отлично!"
    }
}