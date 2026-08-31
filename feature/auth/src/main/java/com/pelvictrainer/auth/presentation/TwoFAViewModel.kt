package com.pelvictrainer.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.auth.AuthRepository
import com.pelvictrainer.domain.subscription.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TwoFAUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val is2FAEnabled: Boolean? = null,
    // Для настройки
    val setupSecret: String? = null,
    val setupQrCodeUrl: String? = null,
    val verifyCode: String = "",
    val backupCodes: List<String> = emptyList(),
    // Для отключения
    val disableCode: String = "",
)

@HiltViewModel
class TwoFAViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TwoFAUiState())
    val state: StateFlow<TwoFAUiState> = _state.asStateFlow()

    // === НОВОЕ: Состояние подписки ===
    val isPremium = subscriptionRepository.subscriptionState.map { it.isPremiumActive }

    init {
        load2FAStatus()
    }

    fun load2FAStatus() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        authRepository.get2FAStatus().fold(
            onSuccess = { enabled ->
                _state.update { it.copy(isLoading = false, is2FAEnabled = enabled) }
            },
            onFailure = { e ->
                _state.update { it.copy(isLoading = false, error = "Ошибка загрузки статуса: ${e.message}") }
            }
        )
    }

    fun startSetup() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        authRepository.setup2FA().fold(
            onSuccess = { data ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        setupSecret = data.secret,
                        setupQrCodeUrl = data.qrCodeUrl,
                        verifyCode = "",
                    )
                }
            },
            onFailure = { e ->
                _state.update { it.copy(isLoading = false, error = "Ошибка: ${e.message}") }
            }
        )
    }

    fun onVerifyCodeChange(code: String) =
        _state.update { it.copy(verifyCode = code, error = null) }

    fun verifySetup() = viewModelScope.launch {
        val secret = _state.value.setupSecret ?: return@launch
        val code = _state.value.verifyCode.trim()

        _state.update { it.copy(isLoading = true, error = null) }
        authRepository.verifySetup2FA(secret, code).fold(
            onSuccess = { codes ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        is2FAEnabled = true,
                        backupCodes = codes,
                        setupSecret = null,
                        setupQrCodeUrl = null,
                        verifyCode = "",
                    )
                }
            },
            onFailure = { e ->
                _state.update { it.copy(isLoading = false, error = "Неверный код: ${e.message}") }
            }
        )
    }

    fun onDisableCodeChange(code: String) =
        _state.update { it.copy(disableCode = code, error = null) }

    fun disable2FA(code: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        authRepository.disable2FA(code).fold(
            onSuccess = {
                _state.update {
                    it.copy(
                        isLoading = false,
                        is2FAEnabled = false,
                        error = null,
                    )
                }
            },
            onFailure = { e ->
                _state.update { it.copy(isLoading = false, error = "Неверный код: ${e.message}") }
            }
        )
    }

    fun clearError() = _state.update { it.copy(error = null) }

    fun regenerateBackupCodes(code: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        authRepository.regenerateBackupCodes(code).fold(
            onSuccess = { codes ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        backupCodes = codes,
                        disableCode = "",
                    )
                }
            },
            onFailure = { e ->
                _state.update { it.copy(isLoading = false, error = "Неверный код: ${e.message}") }
            }
        )
    }

    fun resetSetup() = _state.update {
        it.copy(
            setupSecret = null,
            setupQrCodeUrl = null,
            verifyCode = "",
            error = null,
        )
    }

    // === НОВОЕ: Сброс Premium (для отладки) ===
    fun resetPremium() = viewModelScope.launch {
        try {
            subscriptionRepository.deactivatePremium()
            _state.update { it.copy(error = null) }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Ошибка сброса: ${e.message}") }
        }
    }

    // === НОВОЕ: Проверить подписку с сервера ===
    fun refreshSubscription() = viewModelScope.launch {
        try {
            subscriptionRepository.refreshFromServer()
            _state.update { it.copy(error = null) }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Ошибка обновления: ${e.message}") }
        }
    }
}