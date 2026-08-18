package com.pelvictrainer.data.repository

import android.util.Log
import com.pelvictrainer.domain.auth.AuthRepository
import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.network.LoginRequest
import com.pelvictrainer.network.PelvicApi
import com.pelvictrainer.network.RegisterRequest
import com.pelvictrainer.network.RefreshRequest
import com.pelvictrainer.network.TokenStorage
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

    override suspend fun login(email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.login(LoginRequest(email, password))
                tokenStorage.accessToken = response.accessToken
                tokenStorage.refreshToken = response.refreshToken
                tokenStorage.userEmail = response.user.email
                tokenStorage.userName = response.user.name
                syncHistoryFromServer()
            }
        }

    override suspend fun register(email: String, password: String, name: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.register(RegisterRequest(email, password, name))
                tokenStorage.accessToken = response.accessToken
                tokenStorage.refreshToken = response.refreshToken
                tokenStorage.userEmail = response.user.email
                tokenStorage.userName = response.user.name
                syncHistoryFromServer()
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

    private suspend fun syncHistoryFromServer() {
        try {
            val response = api.getMySessions()
            Log.d(TAG, "Загружено ${response.sessions.size} тренировок с сервера")

            for (sessionDto in response.sessions) {
                val isDuplicate = trainingRepository.hasDuplicateSession(
                    date = sessionDto.completedAt.toEpochMillis(),
                    presetId = sessionDto.presetId.toLong(),
                    duration = sessionDto.durationSeconds.toLong(),
                )

                if (!isDuplicate) {
                    val session = TrainingSession(
                        id = 0L,
                        presetId = sessionDto.presetId.toLong(),
                        date = sessionDto.completedAt.toEpochMillis(),
                        durationSeconds = sessionDto.durationSeconds.toLong(),
                        repeats = sessionDto.repeatsCompleted,
                        synced = true,
                        serverSessionId = sessionDto.id,
                    )
                    trainingRepository.insertFromServer(session)
                    Log.d(TAG, "✅ Добавлена тренировка с сервера (serverId=${sessionDto.id})")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Не удалось загрузить историю с сервера: ${e.message}")
        }
    }
}