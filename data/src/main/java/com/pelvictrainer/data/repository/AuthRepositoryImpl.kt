package com.pelvictrainer.data.repository

import com.pelvictrainer.domain.auth.AuthRepository
import com.pelvictrainer.network.LoginRequest
import com.pelvictrainer.network.PelvicApi
import com.pelvictrainer.network.RegisterRequest
import com.pelvictrainer.network.RefreshRequest
import com.pelvictrainer.network.TokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: PelvicApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.login(LoginRequest(email, password))
                tokenStorage.accessToken = response.accessToken
                tokenStorage.refreshToken = response.refreshToken
                tokenStorage.userEmail = response.user.email
                tokenStorage.userName = response.user.name
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
            }
        }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        val refresh = tokenStorage.refreshToken
        if (refresh != null) {
            runCatching {
                api.logout(RefreshRequest(refresh))
            }
        }
        tokenStorage.clear()
    }

    override suspend fun isLoggedIn(): Boolean = tokenStorage.isLoggedIn

    override suspend fun getCurrentUserEmail(): String? = tokenStorage.userEmail

    override suspend fun getCurrentUserName(): String? = tokenStorage.userName
}