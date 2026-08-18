package com.pelvictrainer.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.domain.subscription.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class StatisticsUiState(
    val totalSessions: Int = 0,
    val totalDurationSeconds: Long = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val averageDurationSeconds: Long = 0,
    val last7DaysSessions: List<Pair<String, Int>> = emptyList(),
    val isLoading: Boolean = false,
    val syncedCount: Int = 0,
    val unsyncedCount: Int = 0,
    val isLoggedIn: Boolean = false,
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val authRepository: com.pelvictrainer.domain.auth.AuthRepository,
    val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    val isPremium = subscriptionRepository.subscriptionState.map { it.isPremiumActive }

    init {
        loadStatistics()
        loadAuthState()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            trainingRepository.getSessions().collect { sessions ->
                _uiState.value = calculateStatistics(sessions)
            }
        }
    }

    private fun loadAuthState() {
        viewModelScope.launch {
            val loggedIn = authRepository.isLoggedIn()
            _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn)
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

    private fun calculateStatistics(sessions: List<TrainingSession>): StatisticsUiState {
        if (sessions.isEmpty()) {
            return StatisticsUiState(
                last7DaysSessions = generateEmptyWeek(),
                isLoggedIn = _uiState.value.isLoggedIn,
            )
        }

        val totalSessions = sessions.size
        val totalDuration = sessions.sumOf { it.durationSeconds }
        val averageDuration = totalDuration / totalSessions
        val (currentStreak, bestStreak) = calculateStreaks(sessions)
        val last7Days = generateLast7DaysData(sessions)

        val syncedCount = sessions.count { it.synced }
        val unsyncedCount = sessions.count { !it.synced }

        return StatisticsUiState(
            totalSessions = totalSessions,
            totalDurationSeconds = totalDuration,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            averageDurationSeconds = averageDuration,
            last7DaysSessions = last7Days,
            isLoading = false,
            syncedCount = syncedCount,
            unsyncedCount = unsyncedCount,
            isLoggedIn = _uiState.value.isLoggedIn,
        )
    }

    private fun calculateStreaks(sessions: List<TrainingSession>): Pair<Int, Int> {
        val trainingDates = sessions.map { session ->
            LocalDate.ofEpochDay(session.date / (24 * 60 * 60 * 1000))
        }.distinct().sortedDescending()

        if (trainingDates.isEmpty()) return Pair(0, 0)

        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 1

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        if (trainingDates.first() == today || trainingDates.first() == yesterday) {
            currentStreak = 1
            for (i in 1 until trainingDates.size) {
                val diff = java.time.temporal.ChronoUnit.DAYS.between(
                    trainingDates[i],
                    trainingDates[i - 1]
                )
                if (diff == 1L) {
                    currentStreak++
                } else {
                    break
                }
            }
        }

        for (i in 1 until trainingDates.size) {
            val diff = java.time.temporal.ChronoUnit.DAYS.between(
                trainingDates[i],
                trainingDates[i - 1]
            )
            if (diff == 1L) {
                tempStreak++
                bestStreak = maxOf(bestStreak, tempStreak)
            } else {
                tempStreak = 1
            }
        }

        bestStreak = maxOf(bestStreak, currentStreak, if (trainingDates.isNotEmpty()) 1 else 0)

        return Pair(currentStreak, bestStreak)
    }

    private fun generateLast7DaysData(sessions: List<TrainingSession>): List<Pair<String, Int>> {
        val today = LocalDate.now()
        val dayNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        return (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val count = sessions.count { session ->
                val sessionDate = LocalDate.ofEpochDay(session.date / (24 * 60 * 60 * 1000))
                sessionDate == date
            }
            Pair(dayNames[(date.dayOfWeek.value - 1) % 7], count)
        }
    }

    private fun generateEmptyWeek(): List<Pair<String, Int>> {
        val today = LocalDate.now()
        val dayNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
        return (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            Pair(dayNames[(date.dayOfWeek.value - 1) % 7], 0)
        }
    }
}