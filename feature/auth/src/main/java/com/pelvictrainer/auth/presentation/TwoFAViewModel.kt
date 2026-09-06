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
    val setupSecret: String? = null,
    val setupQrCodeUrl: String? = null,
    val verifyCode: String = "",
    val backupCodes: List<String> = emptyList(),
    val disableCode: String = "",
)

@HiltViewModel
class TwoFAViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TwoFAUiState())
    val state: StateFlow<TwoFAUiState> = _state.asStateFlow()

    val isPremium = subscriptionRepository.subscriptionState.map { it.isPremiumActive }

    init {
        load2FAStatus()
    }

    fun load2FAStatus() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val result = authRepository.get2FAStatus()
            result.fold(
                onSuccess = { enabled ->
                    _state.update { it.copy(isLoading = false, is2FAEnabled = enabled) }
                },
                onFailure = { e ->
                    // Не крашим приложение — просто показываем неизвестный статус
                    _state.update {
                        it.copy(
                            isLoading = false,
                            is2FAEnabled = null,
                            error = null // Не показываем ошибку, т.к. это не критично
                        )
                    }
                }
            )
        } catch (e: Exception) {
            // Любое необработанное исключение — логируем и не крашим
            _state.update {
                it.copy(
                    isLoading = false,
                    is2FAEnabled = null,
                    error = null
                )
            }
        }
    }

    fun startSetup() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val result = authRepository.setup2FA()
            result.fold(
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
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = "Ошибка: ${e.message}") }
        }
    }

    fun onVerifyCodeChange(code: String) =
        _state.update { it.copy(verifyCode = code, error = null) }

    fun verifySetup() = viewModelScope.launch {
        val secret = _state.value.setupSecret ?: return@launch
        val code = _state.value.verifyCode.trim()

        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val result = authRepository.verifySetup2FA(secret, code)
            result.fold(
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
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = "Ошибка: ${e.message}") }
        }
    }

    fun onDisableCodeChange(code: String) =
        _state.update { it.copy(disableCode = code, error = null) }

    fun disable2FA(code: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val result = authRepository.disable2FA(code)
            result.fold(
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
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = "Ошибка: ${e.message}") }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }

    fun regenerateBackupCodes(code: String) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        try {
            val result = authRepository.regenerateBackupCodes(code)
            result.fold(
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
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, error = "Ошибка: ${e.message}") }
        }
    }

    fun resetSetup() = _state.update {
        it.copy(
            setupSecret = null,
            setupQrCodeUrl = null,
            verifyCode = "",
            error = null,
        )
    }

    fun refreshSubscription() = viewModelScope.launch {
        try {
            subscriptionRepository.refreshFromServer()
        } catch (e: Exception) {
            // Логируем но не крашим
            android.util.Log.w("TwoFAViewModel", "refreshSubscription failed: ${e.message}")
        }
    }
}