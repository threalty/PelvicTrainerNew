package com.pelvictrainer.domain.auth

interface AuthRepository {
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

    // Восстановление пароля
    suspend fun forgotPassword(email: String): Result<Unit>

    // === 2FA: подтверждение при логине ===
    suspend fun verify2FA(userId: Int, code: String): LoginResult
    suspend fun verify2FABackup(userId: Int, code: String): LoginResult

    // === 2FA: управление (требуют авторизацию) ===
    suspend fun get2FAStatus(): Result<Boolean>
    suspend fun setup2FA(): Result<TwoFASetupData>
    suspend fun verifySetup2FA(secret: String, code: String): Result<List<String>>
    suspend fun disable2FA(code: String): Result<Unit>
    suspend fun regenerateBackupCodes(code: String): Result<List<String>>
}

/**
 * Данные для настройки 2FA (секрет и URL для QR-кода)
 */
data class TwoFASetupData(
    val secret: String,
    val qrCodeUrl: String,
)