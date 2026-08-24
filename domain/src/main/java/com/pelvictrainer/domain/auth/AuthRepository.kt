package com.pelvictrainer.domain.auth

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(
        email: String,
        password: String,
        name: String,
        consentPrivacy: Boolean = true,
        consentHealth: Boolean = true,
    ): Result<Unit>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    suspend fun getCurrentUserEmail(): String?
    suspend fun getCurrentUserName(): String?

    // === НОВОЕ: Восстановление пароля ===
    suspend fun forgotPassword(email: String): Result<Unit>
}