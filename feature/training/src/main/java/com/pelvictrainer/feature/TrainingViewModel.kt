package com.pelvictrainer.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.TrainingPhase
import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repository: TrainingRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val audio: TrainingAudio,
    private val haptic: TrainingHaptic
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrainingUiState>(TrainingUiState.Loading)
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    private var currentPreset: TrainingPreset? = null
    private var currentTimeLeft: Int = 0
    private var currentRepCount: Int = 0
    private var currentPhase: TrainingPhase = TrainingPhase.IDLE

    private var voiceEnabled = true
    private var voiceVolume = 0.8f
    private var vibrationEnabled = true
    private var vibrationIntensity = 0.8f

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { prefs ->
                voiceEnabled = prefs.voiceEnabled
                voiceVolume = prefs.voiceVolume
                vibrationEnabled = prefs.vibrationEnabled
                vibrationIntensity = prefs.vibrationIntensity

                audio.setVolume(if (voiceEnabled) voiceVolume else 0f)
                haptic.setEnabled(vibrationEnabled)
                haptic.setIntensity(vibrationIntensity)
            }
        }
    }

    fun loadPreset(presetId: Long) {
        viewModelScope.launch {
            try {
                val preset = repository.getPresetById(presetId)
                currentPreset = preset
                _uiState.value = TrainingUiState.Ready(preset)
            } catch (e: Exception) {
                _uiState.value = TrainingUiState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    fun startTraining(preset: TrainingPreset) {
        currentPreset = preset
        currentRepCount = preset.totalReps
        currentPhase = TrainingPhase.SQUEEZE
        currentTimeLeft = preset.squeezeTime

        _uiState.value = TrainingUiState.Training(
            preset = preset,
            phase = currentPhase,
            progress = 0f,
            timeLeft = currentTimeLeft,
            repsLeft = currentRepCount,
            currentRep = 1
        )

        triggerPhaseFeedback(currentPhase)
        startTimerLogic()
    }

    private fun triggerPhaseFeedback(phase: TrainingPhase) {
        if (voiceEnabled) audio.speakPhase(phase)
        haptic.vibrateForPhase(phase)
    }

    private fun startTimerLogic() {
        viewModelScope.launch {
            while (currentRepCount > 0) {
                when (currentPhase) {
                    TrainingPhase.SQUEEZE -> {
                        runPhase(currentPreset!!.squeezeTime, TrainingPhase.SQUEEZE)
                        currentPhase = TrainingPhase.HOLD
                        currentTimeLeft = currentPreset!!.holdTime
                    }
                    TrainingPhase.HOLD -> {
                        runPhase(currentPreset!!.holdTime, TrainingPhase.HOLD)
                        currentPhase = TrainingPhase.RELAX
                        currentTimeLeft = currentPreset!!.relaxTime
                    }
                    TrainingPhase.RELAX -> {
                        runPhase(currentPreset!!.relaxTime, TrainingPhase.RELAX)
                        currentRepCount--
                        if (currentRepCount > 0) {
                            currentPhase = TrainingPhase.SQUEEZE
                            currentTimeLeft = currentPreset!!.squeezeTime
                        }
                    }
                    else -> break
                }

                if (currentRepCount > 0) {
                    triggerPhaseFeedback(currentPhase)
                    updateUiState()
                }
            }
            finishTraining()
        }
    }

    private suspend fun runPhase(duration: Int, phase: TrainingPhase) {
        for (i in duration downTo 1) {
            currentTimeLeft = i
            val progress = 1f - (i.toFloat() / duration.toFloat())
            updateUiState(progress)
            kotlinx.coroutines.delay(1000L)
        }
    }

    private fun updateUiState(progress: Float = 0f) {
        val preset = currentPreset ?: return
        _uiState.value = TrainingUiState.Training(
            preset = preset,
            phase = currentPhase,
            progress = progress,
            timeLeft = currentTimeLeft,
            repsLeft = currentRepCount,
            currentRep = preset.totalReps - currentRepCount + 1
        )
    }

    private fun finishTraining() {
        viewModelScope.launch {
            triggerPhaseFeedback(TrainingPhase.FINISHED)
            repository.saveTrainingSession(
                presetId = currentPreset?.id ?: 0L,
                completedReps = currentPreset?.totalReps ?: 0,
                durationSeconds = 0L
            )
            _uiState.value = TrainingUiState.Finished(currentPreset!!)
        }
    }

    fun reset() {
        currentPreset?.let { _uiState.value = TrainingUiState.Ready(it) }
    }

    override fun onCleared() {
        super.onCleared()
        audio.shutdown()
    }
}