package com.pelvictrainer.domain.auth

import kotlinx.coroutines.flow.Flow

data class UserAuthState(
    val isLoggedIn: Boolean = false,
    val email: String? = null,
    val name: String? = null,
)

interface AuthRepository {
    // === НОВОЕ: Реактивное состояние авторизации ===
    val userAuthStateFlow: Flow<UserAuthState>

    suspend fun login(email: String, password: String): LoginResult
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

    suspend fun forgotPassword(email: String): Result<Unit>

    // === 2FA ===
    suspend fun verify2FA(userId: Int, code: String): LoginResult
    suspend fun verify2FABackup(userId: Int, code: String): LoginResult

    suspend fun get2FAStatus(): Result<Boolean>
    suspend fun setup2FA(): Result<TwoFASetupData>
    suspend fun verifySetup2FA(secret: String, code: String): Result<List<String>>
    suspend fun disable2FA(code: String): Result<Unit>
    suspend fun regenerateBackupCodes(code: String): Result<List<String>>
}