package com.pelvictrainer.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val sessions: List<TrainingSession> = emptyList(),
    val selectedDate: LocalDate? = null,
    val sessionsForSelectedDate: List<TrainingSession> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            trainingRepository.getSessions().collect { sessions ->
                _uiState.value = _uiState.value.copy(sessions = sessions)
                updateSessionsForSelectedDate()
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        updateSessionsForSelectedDate()
    }

    fun changeMonth(yearMonth: YearMonth) {
        _uiState.value = _uiState.value.copy(currentMonth = yearMonth)
    }

    suspend fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            // Данные уже автоматически обновляются через collect из loadSessions()
            // Здесь просто имитируем задержку для UX
            kotlinx.coroutines.delay(800)
        } finally {
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun updateSessionsForSelectedDate() {
        val selectedDate = _uiState.value.selectedDate ?: return
        val sessionsForDate = _uiState.value.sessions.filter { session ->
            val sessionDate = LocalDate.ofEpochDay(session.date / (24 * 60 * 60 * 1000))
            sessionDate == selectedDate
        }
        _uiState.value = _uiState.value.copy(sessionsForSelectedDate = sessionsForDate)
    }
}