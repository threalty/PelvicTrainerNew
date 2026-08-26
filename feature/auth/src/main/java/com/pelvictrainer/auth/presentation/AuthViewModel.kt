package com.pelvictrainer.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.auth.AuthRepository
import com.pelvictrainer.domain.auth.LoginResult
import com.yandex.metrica.YandexMetrica
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccess: Boolean = false,
    val isLoggedIn: Boolean = false,
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val consentPrivacy: Boolean = false,
    val consentHealth: Boolean = false,
    // === 2FA ===
    val requires2FAUserId: Int? = null,
    val requires2FAEmail: String? = null,
    val twoFACode: String = "",
    val useBackupCode: Boolean = false,
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    companion object { private const val TAG = "AuthViewModel" }

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val loggedIn = authRepository.isLoggedIn()
            _state.update { it.copy(isLoggedIn = loggedIn) }
        }
    }

    fun onEmailChange(email: String) = _state.update { it.copy(email = email, error = null) }
    fun onPasswordChange(pw: String) = _state.update { it.copy(password = pw, error = null) }
    fun onNameChange(name: String) = _state.update { it.copy(name = name, error = null) }
    fun onConsentPrivacyChange(value: Boolean) = _state.update { it.copy(consentPrivacy = value) }
    fun onConsentHealthChange(value: Boolean) = _state.update { it.copy(consentHealth = value) }
    fun onTwoFACodeChange(code: String) = _state.update { it.copy(twoFACode = code, error = null) }
    fun toggleBackupCodeMode() =
        _state.update { it.copy(useBackupCode = !it.useBackupCode, twoFACode = "", error = null) }

    fun reset2FAState() = _state.update {
        it.copy(
            requires2FAUserId = null,
            requires2FAEmail = null,
            twoFACode = "",
            useBackupCode = false,
            error = null,
        )
    }

    fun login() = viewModelScope.launch {
        Log.d(TAG, "🔐 Login clicked for ${_state.value.email}")
        _state.update { it.copy(isLoading = true, error = null) }
        val result = authRepository.login(
            email = _state.value.email.trim(),
            password = _state.value.password,
        )
        Log.d(TAG, "📥 Login result: $result")
        when (result) {
            is LoginResult.Success -> {
                runCatching { YandexMetrica.reportEvent("login_success") }
                _state.update {
                    it.copy(isLoading = false, isLoginSuccess = true, isLoggedIn = true)
                }
            }
            is LoginResult.Requires2FA -> {
                runCatching { YandexMetrica.reportEvent("login_requires_2fa") }
                _state.update {
                    it.copy(
                        isLoading = false,
                        requires2FAUserId = result.userId,
                        requires2FAEmail = result.email,
                        error = null,
                    )
                }
            }
            is LoginResult.Error -> {
                runCatching { YandexMetrica.reportEvent("login_failed") }
                _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }

    fun verify2FACode() = viewModelScope.launch {
        val userId = _state.value.requires2FAUserId ?: return@launch
        val code = _state.value.twoFACode.trim()
        _state.update { it.copy(isLoading = true, error = null) }
        handleVerifyResult(authRepository.verify2FA(userId, code), "2fa_success")
    }

    fun verifyBackupCode() = viewModelScope.launch {
        val userId = _state.value.requires2FAUserId ?: return@launch
        val code = _state.value.twoFACode.trim()
        _state.update { it.copy(isLoading = true, error = null) }
        handleVerifyResult(authRepository.verify2FABackup(userId, code), "2fa_backup_success")
    }

    private fun handleVerifyResult(result: LoginResult, successEvent: String) {
        when (result) {
            is LoginResult.Success -> {
                runCatching { YandexMetrica.reportEvent(successEvent) }
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        isLoggedIn = true,
                        requires2FAUserId = null,
                        requires2FAEmail = null,
                        twoFACode = "",
                    )
                }
            }
            is LoginResult.Requires2FA ->
                _state.update { it.copy(isLoading = false, error = "Неожиданная ошибка") }
            is LoginResult.Error ->
                _state.update { it.copy(isLoading = false, error = result.message) }
        }
    }

    fun register() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        authRepository.register(
            email = _state.value.email.trim(),
            password = _state.value.password,
            name = _state.value.name.trim(),
            consentPrivacy = _state.value.consentPrivacy,
            consentHealth = _state.value.consentHealth,
        ).fold(
            onSuccess = {
                runCatching { YandexMetrica.reportEvent("register_success") }
                _state.update {
                    it.copy(isLoading = false, isLoginSuccess = true, isLoggedIn = true)
                }
            },
            onFailure = { t ->
                runCatching { YandexMetrica.reportEvent("register_failed") }
                _state.update { it.copy(isLoading = false, error = parseError(t)) }
            },
        )
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        _state.update {
            AuthUiState(isLoggedIn = false, email = "", password = "", name = "")
        }
    }

    fun consumeSuccess() = _state.update { it.copy(isLoginSuccess = false) }

    private fun parseError(t: Throwable): String {
        val m = t.message ?: return "Ошибка. Попробуйте ещё раз"
        return when {
            m.contains("409") -> "Пользователь с таким email уже существует"
            m.contains("401") -> "Неверный email или пароль"
            m.contains("Unable to resolve host") || m.contains("Network") -> "Нет соединения"
            m.contains("timeout") -> "Сервер не отвечает"
            else -> "Ошибка. Попробуйте ещё раз"
        }
    }
}