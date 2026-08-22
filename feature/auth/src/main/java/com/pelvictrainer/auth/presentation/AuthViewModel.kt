package com.pelvictrainer.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.auth.AuthRepository
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
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val loggedIn = authRepository.isLoggedIn()
            _state.update { it.copy(isLoggedIn = loggedIn) }
        }
    }

    fun onEmailChange(email: String) =
        _state.update { it.copy(email = email, error = null) }

    fun onPasswordChange(pw: String) =
        _state.update { it.copy(password = pw, error = null) }

    fun onNameChange(name: String) =
        _state.update { it.copy(name = name, error = null) }

    fun onConsentPrivacyChange(value: Boolean) =
        _state.update { it.copy(consentPrivacy = value) }

    fun onConsentHealthChange(value: Boolean) =
        _state.update { it.copy(consentHealth = value) }

    fun login() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        val result = authRepository.login(
            email = _state.value.email.trim(),
            password = _state.value.password,
        )
        result.fold(
            onSuccess = {
                runCatching { YandexMetrica.reportEvent("login_success") }
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        isLoggedIn = true,
                    )
                }
            },
            onFailure = { throwable ->
                runCatching { YandexMetrica.reportEvent("login_failed") }
                _state.update { it.copy(isLoading = false, error = parseError(throwable)) }
            },
        )
    }

    fun register() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, error = null) }
        val result = authRepository.register(
            email = _state.value.email.trim(),
            password = _state.value.password,
            name = _state.value.name.trim(),
            consentPrivacy = _state.value.consentPrivacy,
            consentHealth = _state.value.consentHealth,
        )
        result.fold(
            onSuccess = {
                runCatching { YandexMetrica.reportEvent("register_success") }
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        isLoggedIn = true,
                    )
                }
            },
            onFailure = { throwable ->
                runCatching { YandexMetrica.reportEvent("register_failed") }
                _state.update { it.copy(isLoading = false, error = parseError(throwable)) }
            },
        )
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        _state.update {
            AuthUiState(
                isLoggedIn = false,
                email = "",
                password = "",
                name = "",
            )
        }
    }

    fun consumeSuccess() = _state.update { it.copy(isLoginSuccess = false) }

    private fun parseError(t: Throwable): String {
        val message = t.message ?: return "Ошибка. Попробуйте ещё раз"
        return when {
            message.contains("409", ignoreCase = true) ->
                "Пользователь с таким email уже существует"
            message.contains("401", ignoreCase = true) ->
                "Неверный email или пароль"
            message.contains("Unable to resolve host", ignoreCase = true) ||
                    message.contains("Network", ignoreCase = true) ->
                "Нет соединения с сервером"
            message.contains("timeout", ignoreCase = true) ->
                "Сервер не отвечает. Попробуйте позже"
            else -> "Ошибка. Попробуйте ещё раз"
        }
    }
}