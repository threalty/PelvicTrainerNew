package com.pelvictrainer.workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import com.pelvictrainer.domain.subscription.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class WorkoutsUiState(
    val presets: List<TrainingPreset> = emptyList(),
    val userLevel: TrainingLevel = TrainingLevel.BEGINNER,
    val isLoading: Boolean = true,
    val totalCompletedTrainings: Int = 0,
    val trainingsNeededForNextLevel: Int = 0,
    val trainingsDoneOnCurrentLevel: Int = 0,
    val nextLevel: TrainingLevel? = null,
    val progressToNextLevel: Float = 0f,
    val canTrainToday: Boolean = true
)

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    companion object {
        private const val INTERMEDIATE_THRESHOLD = 10
        private const val ADVANCED_THRESHOLD = 30
    }

    private val _uiState = MutableStateFlow(WorkoutsUiState())
    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    private val _levelUpEvent = MutableStateFlow<TrainingLevel?>(null)
    val levelUpEvent: StateFlow<TrainingLevel?> = _levelUpEvent.asStateFlow()

    // ===== Premium состояние (реактивное) =====
    val isPremium = subscriptionRepository.subscriptionState.map { it.isPremiumActive }

    // ИСПРАВЛЕНО: фильтрация по уровню пресета
    // ИСПРАВЛЕНО: используем метод из репозитория
    val availablePresetIds: StateFlow<List<Long>> = subscriptionRepository.subscriptionState
        .map { subscriptionState ->
            val presets = trainingRepository.getPresets().first()
            android.util.Log.d("WorkoutsVM", "📊 Subscription isPremiumActive: ${subscriptionState.isPremiumActive}")
            android.util.Log.d("WorkoutsVM", "📊 Total presets: ${presets.size}")
            presets.forEach { preset ->
                android.util.Log.d("WorkoutsVM", "  Preset ${preset.id}: ${preset.name}, level=${preset.level}")
            }

            if (subscriptionState.isPremiumActive) {
                // Премиум: все пресеты
                val allIds = presets.map { it.id }
                android.util.Log.d("WorkoutsVM", "✅ Premium: доступные ID = $allIds")
                allIds
            } else {
                // Бесплатный: только BEGINNER
                val beginnerIds = presets
                    .filter { it.level == TrainingLevel.BEGINNER }
                    .map { it.id }
                android.util.Log.d("WorkoutsVM", "🆓 Free: доступные ID = $beginnerIds")
                beginnerIds
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf(1L)
        )

    init {
        loadWorkouts()
        checkDailyLimit()
    }

    private fun checkDailyLimit() {
        viewModelScope.launch {
            subscriptionRepository.subscriptionState.collect { subscriptionState ->
                if (!subscriptionState.isPremiumActive) {
                    // Проверяем последнюю тренировку
                    val sessions = trainingRepository.getSessions().first()
                    val today = LocalDate.now(ZoneId.systemDefault())

                    val hasTrainedToday = sessions.any { session ->
                        // session.date это Long (timestamp в миллисекундах)
                        val sessionDate = Instant.ofEpochMilli(session.date)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        sessionDate == today
                    }

                    _uiState.update { it.copy(canTrainToday = !hasTrainedToday) }
                } else {
                    // Премиум: всегда может тренироваться
                    _uiState.update { it.copy(canTrainToday = true) }
                }
            }
        }
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            val presets = trainingRepository.getPresets().first()
            val sessions = trainingRepository.getSessions().first()
            val totalTrainings = sessions.size

            val userPrefs = userPreferencesRepository.userPreferences.first()
            val currentLevel = userPrefs.trainingLevel

            val targetLevel = levelForTrainingsCount(totalTrainings)

            if (targetLevel.ordinal > currentLevel.ordinal) {
                userPreferencesRepository.updateTrainingLevel(targetLevel)
                _levelUpEvent.value = targetLevel
            }

            val effectiveLevel = if (targetLevel.ordinal > currentLevel.ordinal) targetLevel else currentLevel

            _uiState.update { currentState ->
                currentState.copy(
                    presets = presets,
                    userLevel = effectiveLevel,
                    isLoading = false,
                    totalCompletedTrainings = totalTrainings,
                    trainingsNeededForNextLevel = when (effectiveLevel) {
                        TrainingLevel.BEGINNER -> INTERMEDIATE_THRESHOLD
                        TrainingLevel.INTERMEDIATE -> ADVANCED_THRESHOLD
                        TrainingLevel.ADVANCED -> totalTrainings
                    },
                    trainingsDoneOnCurrentLevel = totalTrainings.coerceAtMost(
                        when (effectiveLevel) {
                            TrainingLevel.BEGINNER -> INTERMEDIATE_THRESHOLD
                            TrainingLevel.INTERMEDIATE -> ADVANCED_THRESHOLD
                            TrainingLevel.ADVANCED -> totalTrainings
                        }
                    ),
                    nextLevel = when (effectiveLevel) {
                        TrainingLevel.BEGINNER -> TrainingLevel.INTERMEDIATE
                        TrainingLevel.INTERMEDIATE -> TrainingLevel.ADVANCED
                        TrainingLevel.ADVANCED -> null
                    },
                    progressToNextLevel = calculateProgress(totalTrainings, effectiveLevel)
                )
            }
        }
    }

    private fun calculateProgress(totalTrainings: Int, level: TrainingLevel): Float {
        val thresholdForNext = when (level) {
            TrainingLevel.BEGINNER -> INTERMEDIATE_THRESHOLD
            TrainingLevel.INTERMEDIATE -> ADVANCED_THRESHOLD
            TrainingLevel.ADVANCED -> totalTrainings
        }

        val doneOnLevel = totalTrainings.coerceAtMost(thresholdForNext)
        return if (thresholdForNext > 0) {
            (doneOnLevel.toFloat() / thresholdForNext).coerceIn(0f, 1f)
        } else {
            1f
        }
    }

    private fun levelForTrainingsCount(count: Int): TrainingLevel {
        return when {
            count >= ADVANCED_THRESHOLD -> TrainingLevel.ADVANCED
            count >= INTERMEDIATE_THRESHOLD -> TrainingLevel.INTERMEDIATE
            else -> TrainingLevel.BEGINNER
        }
    }

    fun onLevelUpShown() {
        _levelUpEvent.value = null
    }
}