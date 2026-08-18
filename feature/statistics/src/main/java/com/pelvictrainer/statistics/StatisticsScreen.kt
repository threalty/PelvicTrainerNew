package com.pelvictrainer.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.designsystem.components.AnimatedCounter
import com.pelvictrainer.designsystem.components.EmptyState
import com.pelvictrainer.designsystem.components.PremiumLockedCard
import com.pelvictrainer.designsystem.components.PullToRefreshBox
import com.pelvictrainer.designsystem.util.rememberHapticHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onNavigateToWorkouts: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val hapticHelper = rememberHapticHelper()
    val coroutineScope = rememberCoroutineScope()

    val isPremium by viewModel.isPremium.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Статистика") })
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = {
                coroutineScope.launch {
                    viewModel.refresh()
                }
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            if (uiState.totalSessions == 0 && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.FitnessCenter,
                        title = "Начните свою первую тренировку",
                        description = "После тренировок здесь появится ваша статистика: общее время, серия дней, лучшая серия и график за 7 дней",
                        primaryActionText = "Начать тренировку",
                        onPrimaryActionClick = {
                            hapticHelper.mediumTap()
                            onNavigateToWorkouts()
                        }
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedVisibility(
                            visible = true,
                            modifier = Modifier.weight(1f),
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 2 }
                            )
                        ) {
                            StatCard(
                                icon = Icons.Default.FitnessCenter,
                                title = "Тренировок"
                            ) {
                                AnimatedCounter(
                                    targetValue = uiState.totalSessions,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = true,
                            modifier = Modifier.weight(1f),
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = 100
                                )
                            )
                        ) {
                            StatCard(
                                icon = Icons.Default.LocalFireDepartment,
                                title = "Серия"
                            ) {
                                AnimatedCounter(
                                    targetValue = uiState.currentStreak,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    suffix = " дн"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnimatedVisibility(
                            visible = true,
                            modifier = Modifier.weight(1f),
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = 200
                                )
                            )
                        ) {
                            StatCard(
                                icon = Icons.Default.EmojiEvents,
                                title = "Лучшая серия"
                            ) {
                                AnimatedCounter(
                                    targetValue = uiState.bestStreak,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    suffix = " дн"
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = true,
                            modifier = Modifier.weight(1f),
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(
                                    durationMillis = 300,
                                    delayMillis = 300
                                )
                            )
                        ) {
                            StatCard(
                                icon = Icons.Default.Schedule,
                                title = "Всего времени"
                            ) {
                                Text(
                                    text = formatDuration(uiState.totalDurationSeconds),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isPremium) {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = 400
                                )
                            )
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Последние 7 дней",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    WeekBarChart(
                                        data = uiState.last7DaysSessions,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        PremiumLockedCard(
                            title = "График тренировок",
                            description = "Детальная статистика по дням доступна в Premium",
                            onUpgradeClick = onNavigateToPremium,
                        )
                    }

                    if (uiState.isLoggedIn) {
                        Spacer(modifier = Modifier.height(16.dp))

                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 3 },
                                animationSpec = tween(
                                    durationMillis = 400,
                                    delayMillis = 500
                                )
                            )
                        ) {
                            SyncStatusCard(
                                syncedCount = uiState.syncedCount,
                                unsyncedCount = uiState.unsyncedCount,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    valueContent: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            valueContent()
        }
    }
}

@Composable
private fun SyncStatusCard(
    syncedCount: Int,
    unsyncedCount: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (unsyncedCount > 0) {
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (unsyncedCount > 0) Icons.Default.CloudOff else Icons.Default.Cloud,
                    contentDescription = null,
                    tint = if (unsyncedCount > 0) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (unsyncedCount > 0) "Ожидает синхронизации" else "Синхронизация",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "☁️ Синхронизировано",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$syncedCount тренировок",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (unsyncedCount > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "⏳ Ожидает сети",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$unsyncedCount тренировок",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            if (unsyncedCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Тренировки будут отправлены на сервер автоматически при подключении к интернету",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun WeekBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val maxValue = data.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { (dayLabel, count) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .width(24.dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barHeight = if (count > 0) {
                            size.height * (count.toFloat() / maxValue)
                        } else {
                            size.height * 0.05f
                        }

                        val barTop = size.height - barHeight

                        drawRect(
                            brush = if (count > 0) {
                                Brush.verticalGradient(
                                    colors = listOf(primaryColor, primaryColor.copy(alpha = 0.6f)),
                                    startY = barTop,
                                    endY = size.height
                                )
                            } else {
                                Brush.verticalGradient(
                                    colors = listOf(surfaceColor, surfaceColor),
                                    startY = barTop,
                                    endY = size.height
                                )
                            },
                            topLeft = Offset(0f, barTop),
                            size = Size(size.width, barHeight)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}ч ${minutes}м"
        minutes > 0 -> "${minutes}м"
        else -> "${seconds}с"
    }
}