package com.pelvictrainer.data.repository

import android.util.Log
import com.pelvictrainer.domain.auth.AuthRepository
import com.pelvictrainer.domain.auth.LoginResult
import com.pelvictrainer.domain.auth.TwoFASetupData
import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.network.ForgotPasswordRequest
import com.pelvictrainer.network.LoginRequest
import com.pelvictrainer.network.PelvicApi
import com.pelvictrainer.network.RegisterRequest
import com.pelvictrainer.network.RefreshRequest
import com.pelvictrainer.network.TokenStorage
import com.pelvictrainer.network.dto.TwoFADisableRequest
import com.pelvictrainer.network.dto.TwoFAVerifyBackupResponse
import com.pelvictrainer.network.dto.TwoFAVerifyLoginResponse
import com.pelvictrainer.network.dto.VerifyBackupRequest
import com.pelvictrainer.network.dto.VerifyLoginRequest
import com.pelvictrainer.network.dto.VerifySetupRequest
import com.pelvictrainer.network.dto.toEpochMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: PelvicApi,
    private val tokenStorage: TokenStorage,
    private val trainingRepository: TrainingRepository,
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun login(email: String, password: String): LoginResult =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔐 Login attempt for: $email")
                val response = api.login(LoginRequest(email, password))
                Log.d(TAG, "📥 Login response: requires2fa=${response.requires2fa}, userId=${response.userId}")

                val userId = response.userId
                if (response.requires2fa && userId != null) {
                    Log.d(TAG, "🔐 2FA required for userId=$userId")
                    return@withContext LoginResult.Requires2FA(userId = userId, email = email)
                }

                val accessToken = response.accessToken
                val refreshToken = response.refreshToken
                val user = response.user

                if (accessToken == null || refreshToken == null || user == null) {
                    return@withContext LoginResult.Error("Не удалось получить данные авторизации")
                }

                tokenStorage.accessToken = accessToken
                tokenStorage.refreshToken = refreshToken
                tokenStorage.userEmail = user.email
                tokenStorage.userName = user.name
                syncHistoryFromServer()

                LoginResult.Success
            } catch (e: Exception) {
                Log.e(TAG, "❌ Login failed", e)
                LoginResult.Error(parseErrorMessage(e))
            }
        }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        consentPrivacy: Boolean,
        consentHealth: Boolean,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.register(
                    RegisterRequest(
                        email = email,
                        password = password,
                        name = name,
                        consentPrivacy = consentPrivacy,
                        consentHealth = consentHealth,
                        consentAge = true,
                    )
                )
                val accessToken = response.accessToken
                val refreshToken = response.refreshToken
                val user = response.user
                if (accessToken != null && refreshToken != null && user != null) {
                    tokenStorage.accessToken = accessToken
                    tokenStorage.refreshToken = refreshToken
                    tokenStorage.userEmail = user.email
                    tokenStorage.userName = user.name
                    syncHistoryFromServer()
                }
            }
        }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        val refresh = tokenStorage.refreshToken
        if (refresh != null) {
            runCatching { api.logout(RefreshRequest(refresh)) }
        }
        tokenStorage.clear()
    }

    override suspend fun isLoggedIn(): Boolean = tokenStorage.isLoggedIn
    override suspend fun getCurrentUserEmail(): String? = tokenStorage.userEmail
    override suspend fun getCurrentUserName(): String? = tokenStorage.userName

    override suspend fun forgotPassword(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { api.forgotPassword(ForgotPasswordRequest(email)); Unit }
        }

    override suspend fun verify2FA(userId: Int, code: String): LoginResult =
        withContext(Dispatchers.IO) {
            try {
                val response: TwoFAVerifyLoginResponse = api.verify2FALogin(
                    VerifyLoginRequest(userId = userId, code = code)
                )
                tokenStorage.accessToken = response.accessToken
                tokenStorage.refreshToken = response.refreshToken
                tokenStorage.userEmail = response.email
                tokenStorage.userName = response.name ?: ""
                syncHistoryFromServer()
                LoginResult.Success
            } catch (e: Exception) {
                Log.e(TAG, "❌ 2FA verify failed", e)
                LoginResult.Error(parseErrorMessage(e))
            }
        }

    override suspend fun verify2FABackup(userId: Int, code: String): LoginResult =
        withContext(Dispatchers.IO) {
            try {
                val response: TwoFAVerifyBackupResponse = api.verify2FABackup(
                    VerifyBackupRequest(userId = userId, code = code)
                )
                tokenStorage.accessToken = response.accessToken
                tokenStorage.refreshToken = response.refreshToken
                tokenStorage.userEmail = response.email
                tokenStorage.userName = response.name ?: ""
                syncHistoryFromServer()
                LoginResult.Success
            } catch (e: Exception) {
                Log.e(TAG, "❌ Backup verify failed", e)
                LoginResult.Error(parseErrorMessage(e))
            }
        }

    override suspend fun get2FAStatus(): Result<Boolean> =
        withContext(Dispatchers.IO) { runCatching { api.get2FAStatus().enabled } }

    override suspend fun setup2FA(): Result<TwoFASetupData> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.setup2FA()
                TwoFASetupData(secret = response.secret, qrCodeUrl = response.qrCodeUrl)
            }
        }

    override suspend fun verifySetup2FA(secret: String, code: String): Result<List<String>> =
        withContext(Dispatchers.IO) {
            runCatching { api.verifySetup2FA(VerifySetupRequest(secret, code)).backupCodes }
        }

    override suspend fun disable2FA(code: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching { api.disable2FA(TwoFADisableRequest(code)); Unit }
        }

    override suspend fun regenerateBackupCodes(code: String): Result<List<String>> =
        withContext(Dispatchers.IO) {
            runCatching { api.regenerateBackupCodes(TwoFADisableRequest(code)).backupCodes }
        }

    private suspend fun syncHistoryFromServer() {
        try {
            val response = api.getMySessions()
            for (sessionDto in response.sessions) {
                val isDuplicate = trainingRepository.hasDuplicateSession(
                    date = sessionDto.completedAt.toEpochMillis(),
                    presetId = sessionDto.presetId.toLong(),
                    duration = sessionDto.durationSeconds.toLong(),
                )
                if (!isDuplicate) {
                    trainingRepository.insertFromServer(
                        TrainingSession(
                            id = 0L,
                            presetId = sessionDto.presetId.toLong(),
                            date = sessionDto.completedAt.toEpochMillis(),
                            durationSeconds = sessionDto.durationSeconds.toLong(),
                            repeats = sessionDto.repeatsCompleted,
                            synced = true,
                            serverSessionId = sessionDto.id,
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Sync failed: ${e.message}")
        }
    }

    private fun parseErrorMessage(e: Throwable): String {
        val message = e.message ?: return "Ошибка. Попробуйте ещё раз"
        return when {
            message.contains("409") -> "Пользователь с таким email уже существует"
            message.contains("401") -> "Неверный email или пароль"
            message.contains("Unable to resolve host") || message.contains("Network") ->
                "Нет соединения с сервером"
            message.contains("timeout") -> "Сервер не отвечает"
            else -> message.takeIf { it.isNotBlank() } ?: "Ошибка. Попробуйте ещё раз"
        }
    }
}