package com.pelvictrainer.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class HomeUiState(
    val weeklyGoal: Int = 3,
    val completedThisWeek: Int = 0,
    val currentStreak: Int = 0,
    val totalTrainings: Int = 0
) {
    val progress: Float
        get() = if (weeklyGoal > 0) (completedThisWeek.toFloat() / weeklyGoal).coerceIn(0f, 1f) else 0f
    val isGoalReached: Boolean
        get() = completedThisWeek >= weeklyGoal
    val remainingTrainings: Int
        get() = (weeklyGoal - completedThisWeek).coerceAtLeast(0)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.userPreferences,
                trainingRepository.getSessions()
            ) { prefs, sessions ->
                val now = LocalDate.now()
                val weekAgo = now.minusDays(6)
                val weekStartMillis = weekAgo.atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()

                val sessionsThisWeek = sessions.filter { it.date >= weekStartMillis }
                val completedThisWeek = sessionsThisWeek.size

                val currentStreak = calculateStreak(sessions)

                HomeUiState(
                    weeklyGoal = prefs.weeklyGoal,
                    completedThisWeek = completedThisWeek,
                    currentStreak = currentStreak,
                    totalTrainings = sessions.size
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun calculateStreak(sessions: List<com.pelvictrainer.domain.model.TrainingSession>): Int {
        if (sessions.isEmpty()) return 0

        val trainingDates = sessions.map { session ->
            java.time.Instant.ofEpochMilli(session.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.distinct().sortedDescending()

        if (trainingDates.isEmpty()) return 0

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        if (trainingDates.first() != today && trainingDates.first() != yesterday) {
            return 0
        }

        var streak = 1
        for (i in 1 until trainingDates.size) {
            val diff = java.time.temporal.ChronoUnit.DAYS.between(trainingDates[i], trainingDates[i - 1])
            if (diff == 1L) {
                streak++
            } else {
                break
            }
        }

        return streak
    }
}

@Composable
fun HomeScreen(
    onOpenWorkoutsList: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Добро пожаловать!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Сегодня отличный день для тренировки",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ===== Карточка недельной цели =====
        WeeklyGoalCard(uiState = uiState)

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Быстрый старт =====
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenWorkoutsList),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Начать тренировку",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Выберите уровень сложности и начните прямо сейчас",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onOpenWorkoutsList,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Выбрать тренировку")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Streak карточка =====
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Серия дней",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${uiState.currentStreak} ${pluralizeDays(uiState.currentStreak)} подряд",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "🔥",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ===== Статистика =====
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Общая статистика",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = "Всего тренировок",
                        value = uiState.totalTrainings.toString()
                    )
                    StatItem(
                        label = "Серия",
                        value = "${uiState.currentStreak}д"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun WeeklyGoalCard(uiState: HomeUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (uiState.isGoalReached) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (uiState.isGoalReached) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.FitnessCenter
                    },
                    contentDescription = null,
                    tint = if (uiState.isGoalReached) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (uiState.isGoalReached) {
                            "🎉 Цель достигнута!"
                        } else {
                            "Недельная цель"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isGoalReached) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                    Text(
                        text = if (uiState.isGoalReached) {
                            "Отличная работа! Продолжайте в том же духе"
                        } else {
                            "Осталось ${uiState.remainingTrainings} ${pluralizeTrainingsShort(uiState.remainingTrainings)}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (uiState.isGoalReached) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        } else {
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = if (uiState.isGoalReached) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.primary
                },
                trackColor = if (uiState.isGoalReached) {
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${uiState.completedThisWeek} / ${uiState.weeklyGoal} ${pluralizeTrainingsShort(uiState.weeklyGoal)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (uiState.isGoalReached) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                }
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun pluralizeDays(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    return when {
        mod10 == 1 && mod100 != 11 -> "день"
        mod10 in 2..4 && mod100 !in 12..14 -> "дня"
        else -> "дней"
    }
}

private fun pluralizeTrainingsShort(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    return when {
        mod10 == 1 && mod100 != 11 -> "тренировка"
        mod10 in 2..4 && mod100 !in 12..14 -> "тренировки"
        else -> "тренировок"
    }
}