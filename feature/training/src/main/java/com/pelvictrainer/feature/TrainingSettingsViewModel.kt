package com.pelvictrainer.feature.training // Или com.pelvictrainer.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingSettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // Загрузка уровня
            launch {
                userPreferencesRepository.getTrainingLevel().collect { level ->
                    _uiState.value = _uiState.value.copy(
                        currentLevel = level ?: TrainingLevel.BEGINNER
                    )
                }
            }
            // Загрузка статуса уведомлений (если есть такой метод в репозитории)
            // Если метода getNotificationsEnabled нет, закомментируйте этот блок
            /*
            launch {
                userPreferencesRepository.getNotificationsEnabled().collect { enabled ->
                    _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
                }
            }
            */
        }
    }

    fun updateTrainingLevel(level: TrainingLevel) {
        viewModelScope.launch {
            userPreferencesRepository.updateTrainingLevel(level)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            // Убедитесь, что этот метод существует в UserPreferencesRepository
            // Если нет, создайте его аналогично updateTrainingLevel
            // userPreferencesRepository.updateNotificationsEnabled(enabled)
        }
    }
}

data class SettingsUiState(
    val currentLevel: TrainingLevel = TrainingLevel.BEGINNER,
    val notificationsEnabled: Boolean = true
)