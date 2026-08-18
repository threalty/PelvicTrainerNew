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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutsUiState(
    val presets: List<TrainingPreset> = emptyList(),
    val userLevel: TrainingLevel = TrainingLevel.BEGINNER,
    val isLoading: Boolean = true,
    val totalCompletedTrainings: Int = 0,
    val trainingsNeededForNextLevel: Int = 0,
    val trainingsDoneOnCurrentLevel: Int = 0,
    val nextLevel: TrainingLevel? = null,
    val progressToNextLevel: Float = 0f
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

    // ===== Premium состояние (реактивные) =====
    val isPremium = subscriptionRepository.subscriptionState.map { it.isPremiumActive }

    // ИСПРАВЛЕНО: теперь реактивный — обновляется при изменении подписки
    val availablePresetIds: StateFlow<List<Long>> = subscriptionRepository.subscriptionState
        .map { state ->
            if (state.isPremiumActive) listOf(1L, 2L, 3L) else listOf(1L)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = listOf(1L)
        )

    init {
        loadWorkouts()
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

            _uiState.value = buildUiState(
                presets = presets,
                level = effectiveLevel,
                totalTrainings = totalTrainings
            )
        }
    }

    private fun buildUiState(
        presets: List<TrainingPreset>,
        level: TrainingLevel,
        totalTrainings: Int
    ): WorkoutsUiState {
        val nextLevel = when (level) {
            TrainingLevel.BEGINNER -> TrainingLevel.INTERMEDIATE
            TrainingLevel.INTERMEDIATE -> TrainingLevel.ADVANCED
            TrainingLevel.ADVANCED -> null
        }

        val thresholdForNext = when (level) {
            TrainingLevel.BEGINNER -> INTERMEDIATE_THRESHOLD
            TrainingLevel.INTERMEDIATE -> ADVANCED_THRESHOLD
            TrainingLevel.ADVANCED -> totalTrainings
        }

        val doneOnLevel = totalTrainings.coerceAtMost(thresholdForNext)
        val progress = if (thresholdForNext > 0) {
            (doneOnLevel.toFloat() / thresholdForNext).coerceIn(0f, 1f)
        } else {
            1f
        }

        return WorkoutsUiState(
            presets = presets,
            userLevel = level,
            isLoading = false,
            totalCompletedTrainings = totalTrainings,
            trainingsNeededForNextLevel = thresholdForNext,
            trainingsDoneOnCurrentLevel = doneOnLevel,
            nextLevel = nextLevel,
            progressToNextLevel = progress
        )
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