package com.pelvictrainer.network

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header(HEADER_RETRY) != null) {
            tokenStorage.clear()
            return null
        }

        val refresh = tokenStorage.refreshToken ?: return null

        return runBlocking {
            try {
                val body = """{"refresh_token":"$refresh"}"""
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(BASE_URL + "api/v1/auth/refresh")
                    .post(body)
                    .build()

                val res = OkHttpClient().newCall(request).execute()
                if (!res.isSuccessful) {
                    tokenStorage.clear()
                    return@runBlocking null
                }

                val newToken = JSONObject(res.body?.string() ?: "")
                    .optString("access_token")

                if (newToken.isEmpty()) {
                    tokenStorage.clear()
                    return@runBlocking null
                }

                tokenStorage.accessToken = newToken

                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .header(HEADER_RETRY, "1")
                    .build()
            } catch (e: Exception) {
                tokenStorage.clear()
                null
            }
        }
    }

    private companion object {
        const val BASE_URL = "https://api.pelvictrainer.ru/"
        const val HEADER_RETRY = "X-Retry-Auth"
    }
}
