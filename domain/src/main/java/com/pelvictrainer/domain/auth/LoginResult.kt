package com.pelvictrainer.domain.auth

/**
 * Результат попытки входа.
 * Может быть:
 * - [Success] — вход успешен, токены сохранены
 * - [Requires2FA] — требуется код 2FA, нужно вызвать verify2FA()
 * - [Error] — ошибка входа
 */
sealed class LoginResult {
    object Success : LoginResult()
    data class Requires2FA(val userId: Int, val email: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}