package com.pelvictrainer.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.AccentColor
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
    private val userPreferencesRepository: UserPreferencesRepository
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
        }
    }

    fun updateAccentColor(color: AccentColor) {
        viewModelScope.launch {
            userPreferencesRepository.updateAccentColor(color)
        }
    }

    fun updateVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateVoiceEnabled(enabled)
        }
    }

    fun updateVoiceVolume(volume: Float) {
        viewModelScope.launch {
            userPreferencesRepository.updateVoiceVolume(volume)
        }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateVibrationEnabled(enabled)
        }
    }

    fun updateVibrationIntensity(intensity: Float) {
        viewModelScope.launch {
            userPreferencesRepository.updateVibrationIntensity(intensity)
        }
    }
}