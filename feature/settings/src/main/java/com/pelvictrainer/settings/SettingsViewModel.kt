package com.pelvictrainer.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.analytics.AnalyticsTracker
import com.pelvictrainer.domain.model.AccentColor
import com.pelvictrainer.domain.model.BackgroundSound
import com.pelvictrainer.domain.model.ReminderConfig
import com.pelvictrainer.domain.model.ThemeMode
import com.pelvictrainer.domain.model.UserPreferences
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserPreferences())
    val uiState: StateFlow<UserPreferences> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { prefs ->
                _uiState.value = prefs
            }
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.updateThemeMode(mode)
            analytics.trackEvent("theme_changed", mapOf("theme_mode" to mode.name))
        }
    }

    fun updateAccentColor(color: AccentColor) {
        viewModelScope.launch {
            userPreferencesRepository.updateAccentColor(color)
            analytics.trackEvent("accent_changed", mapOf("accent_color" to color.name))
        }
    }

    fun updateVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateVoiceEnabled(enabled)
            analytics.trackEvent("voice_toggled", mapOf("enabled" to enabled))
        }
    }

    fun updateVoiceVolume(volume: Float) {
        viewModelScope.launch { userPreferencesRepository.updateVoiceVolume(volume) }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateVibrationEnabled(enabled)
            analytics.trackEvent("vibration_toggled", mapOf("enabled" to enabled))
        }
    }

    fun updateVibrationIntensity(intensity: Float) {
        viewModelScope.launch { userPreferencesRepository.updateVibrationIntensity(intensity) }
    }

    fun updateRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateRemindersEnabled(enabled)
            val eventName = if (enabled) "reminder_enabled" else "reminder_disabled"
            analytics.trackEvent(eventName)
        }
    }

    fun addReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val config = ReminderConfig(hour, minute)
            if (config !in _uiState.value.reminderTimes) {
                userPreferencesRepository.addReminderTime(config)
                analytics.trackEvent(
                    "reminder_time_added",
                    mapOf("hour" to hour, "minute" to minute)
                )
            }
        }
    }

    fun removeReminderTime(config: ReminderConfig) {
        viewModelScope.launch {
            userPreferencesRepository.removeReminderTime(config)
            analytics.trackEvent("reminder_time_removed")
        }
    }

    fun toggleReminderDay(day: Int) {
        viewModelScope.launch {
            val currentDays = _uiState.value.reminderDaysOfWeek.toMutableList()
            if (day in currentDays) {
                if (currentDays.size > 1) currentDays.remove(day)
            } else {
                currentDays.add(day)
            }
            userPreferencesRepository.updateReminderDaysOfWeek(currentDays.sorted())
            analytics.trackEvent(
                "reminder_days_changed",
                mapOf("days_count" to currentDays.size)
            )
        }
    }

    fun updateWeeklyGoal(goal: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateWeeklyGoal(goal)
            analytics.trackEvent("goal_updated", mapOf("goal_value" to goal))
        }
    }

    fun updateBackgroundSound(sound: BackgroundSound) {
        viewModelScope.launch {
            userPreferencesRepository.updateBackgroundSound(sound)
            analytics.trackEvent("sound_changed", mapOf("sound_type" to sound.name))
        }
    }
}