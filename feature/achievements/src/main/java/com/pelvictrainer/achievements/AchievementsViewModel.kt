package com.pelvictrainer.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.Achievement
import com.pelvictrainer.domain.model.AchievementType
import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class AchievementsUiState(
    val achievements: List<Achievement> = emptyList(),
    val unlockedCount: Int = 0,
    val totalCount: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AchievementsUiState())
    val uiState: StateFlow<AchievementsUiState> = _uiState.asStateFlow()

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            trainingRepository.getSessions().collect { sessions ->
                val achievements = calculateAchievements(sessions)
                _uiState.value = AchievementsUiState(
                    achievements = achievements,
                    unlockedCount = achievements.count { it.isUnlocked },
                    totalCount = achievements.size,
                    isLoading = false
                )
            }
        }
    }

    suspend fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            kotlinx.coroutines.delay(800)
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun calculateAchievements(sessions: List<TrainingSession>): List<Achievement> {
        val totalSessions = sessions.size
        val totalSeconds = sessions.sumOf { it.durationSeconds }
        val (bestStreak, _) = calculateStreaks(sessions)

        return listOf(
            Achievement(
                type = AchievementType.FIRST_TRAINING,
                title = "Первый шаг",
                description = "Завершите первую тренировку",
                icon = "🎯",
                targetValue = 1,
                currentValue = if (sessions.isNotEmpty()) 1 else 0,
                isUnlocked = sessions.isNotEmpty()
            ),
            Achievement(
                type = AchievementType.TRAINING_COUNT_10,
                title = "Десятка",
                description = "Завершите 10 тренировок",
                icon = "🔟",
                targetValue = 10,
                currentValue = totalSessions.coerceAtMost(10),
                isUnlocked = totalSessions >= 10
            ),
            Achievement(
                type = AchievementType.TRAINING_COUNT_50,
                title = "Полтинник",
                description = "Завершите 50 тренировок",
                icon = "🏃",
                targetValue = 50,
                currentValue = totalSessions.coerceAtMost(50),
                isUnlocked = totalSessions >= 50
            ),
            Achievement(
                type = AchievementType.TRAINING_COUNT_100,
                title = "Сотня",
                description = "Завершите 100 тренировок",
                icon = "💯",
                targetValue = 100,
                currentValue = totalSessions.coerceAtMost(100),
                isUnlocked = totalSessions >= 100
            ),
            Achievement(
                type = AchievementType.STREAK_7_DAYS,
                title = "Неделя силы",
                description = "Тренируйтесь 7 дней подряд",
                icon = "🔥",
                targetValue = 7,
                currentValue = bestStreak.coerceAtMost(7),
                isUnlocked = bestStreak >= 7
            ),
            Achievement(
                type = AchievementType.STREAK_30_DAYS,
                title = "Месяц упорства",
                description = "Тренируйтесь 30 дней подряд",
                icon = "📅",
                targetValue = 30,
                currentValue = bestStreak.coerceAtMost(30),
                isUnlocked = bestStreak >= 30
            ),
            Achievement(
                type = AchievementType.STREAK_100_DAYS,
                title = "Легенда",
                description = "Тренируйтесь 100 дней подряд",
                icon = "🏆",
                targetValue = 100,
                currentValue = bestStreak.coerceAtMost(100),
                isUnlocked = bestStreak >= 100
            ),
            Achievement(
                type = AchievementType.TOTAL_TIME_1_HOUR,
                title = "Час работы",
                description = "Потратьте на тренировки 1 час",
                icon = "⏱️",
                targetValue = 3600,
                currentValue = totalSeconds.toInt().coerceAtMost(3600),
                isUnlocked = totalSeconds >= 3600
            )
        )
    }

    private fun calculateStreaks(sessions: List<TrainingSession>): Pair<Int, Int> {
        val trainingDates = sessions.map { session ->
            LocalDate.ofEpochDay(session.date / (24 * 60 * 60 * 1000))
        }.distinct().sortedDescending()

        if (trainingDates.isEmpty()) return Pair(0, 0)

        var bestStreak = 1
        var tempStreak = 1

        for (i in 1 until trainingDates.size) {
            val diff = ChronoUnit.DAYS.between(trainingDates[i], trainingDates[i - 1])
            if (diff == 1L) {
                tempStreak++
                bestStreak = maxOf(bestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }

        return Pair(bestStreak, bestStreak)
    }
}