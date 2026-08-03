package com.pelvictrainer.feature.training

import android.animation.ValueAnimator
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.domain.model.TrainingPhase
import com.pelvictrainer.domain.model.TrainingPreset
import kotlinx.coroutines.launch

@Composable
fun TrainingScreen(
    presetId: Long,
    onNavigateBack: () -> Unit,
    viewModel: TrainingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Инициализация тренировки при первом запуске экрана
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
                            androidx.compose.material.icons.Icons.Default.ArrowBack,
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
                is TrainingUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is TrainingUiState.Ready -> {
                    TrainingContent(
                        preset = state.preset,
                        phase = TrainingPhase.IDLE,
                        progress = 0f,
                        timeLeft = 0,
                        repsLeft = state.preset.totalReps,
                        onStart = { viewModel.startTraining(state.preset) }
                    )
                }
                is TrainingUiState.Training -> {
                    TrainingContent(
                        preset = state.preset,
                        phase = state.phase,
                        progress = state.progress,
                        timeLeft = state.timeLeft,
                        repsLeft = state.repsLeft,
                        onStart = {}
                    )
                }
                is TrainingUiState.Finished -> {
                    TrainingFinishedContent(
                        preset = state.preset,
                        onRestart = { viewModel.startTraining(state.preset) },
                        onBack = onNavigateBack
                    )
                }
                is TrainingUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка: ${state.message}")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Назад")
                        }
                    }
                }
            }
        }
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = preset.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Кольцо прогресса
        TrainingRing(
            phase = phase,
            progress = progress,
            totalSqueezeTime = preset.squeezeTime,
            totalHoldTime = preset.holdTime,
            totalRelaxTime = preset.relaxTime
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Текст инструкции
        Text(
            text = getInstructionText(phase, timeLeft),
            style = MaterialTheme.typography.titleLarge,
            color = getPhaseColor(phase)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Осталось повторений: $repsLeft",
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
private fun TrainingRing(
    phase: TrainingPhase,
    progress: Float,
    totalSqueezeTime: Int,
    totalHoldTime: Int,
    totalRelaxTime: Int
) {
    val strokeWidth = 28.dp
    val size = 240.dp

    // Анимация для фазы сжатия (увеличение кольца)
    val squeezeProgress by animateFloatAsState(
        targetValue = if (phase == TrainingPhase.SQUEEZE) progress else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "squeezeProgress"
    )

    // Анимация для фазы удержания (пульсация)
    val holdScale by animateFloatAsState(
        targetValue = if (phase == TrainingPhase.HOLD) 1.1f else 1f,
        animationSpec = spring(),
        label = "holdScale"
    )

    // Анимация для фазы расслабления (уменьшение кольца)
    val relaxProgress by animateFloatAsState(
        targetValue = if (phase == TrainingPhase.RELAX) progress else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "relaxProgress"
    )

    Canvas(
        modifier = Modifier
            .size(size)
            .scale(if (phase == TrainingPhase.HOLD) holdScale else 1f)
    ) {
        val diameter = size.toPx()
        val radius = diameter / 2
        val center = Offset(radius, radius)
        val strokeWidthPx = strokeWidth.toPx()

        // Фоновое кольцо
        drawCircle(
            color = MaterialTheme.colorScheme.surfaceVariant,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidthPx)
        )

        // Активное кольцо
        val sweepAngle = when (phase) {
            TrainingPhase.SQUEEZE -> 360f * squeezeProgress
            TrainingPhase.HOLD -> 360f
            TrainingPhase.RELAX -> 360f * (1f - relaxProgress)
            TrainingPhase.IDLE -> 0f
        }

        if (sweepAngle > 0) {
            drawArc(
                color = getPhaseColor(phase),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(diameter, diameter),
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round
                )
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
            androidx.compose.material.icons.Icons.Default.CheckCircle,
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
        TrainingPhase.IDLE -> "Нажмите кнопку начала"
        TrainingPhase.SQUEEZE -> "Сжать! ($timeLeft сек)"
        TrainingPhase.HOLD -> "Держать! ($timeLeft сек)"
        TrainingPhase.RELAX -> "Расслабить! ($timeLeft сек)"
        TrainingPhase.FINISHED -> "Отдых между подходами..."
    }
}

private fun getPhaseColor(phase: TrainingPhase): Color {
    return when (phase) {
        TrainingPhase.SQUEEZE -> MaterialTheme.colorScheme.primary
        TrainingPhase.HOLD -> MaterialTheme.colorScheme.secondary
        TrainingPhase.RELAX -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurface
    }
}