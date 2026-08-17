package com.pelvictrainer.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Не добавляем токен к эндпоинтам авторизации
        if (original.url.encodedPath.contains("/auth/")) {
            return chain.proceed(original)
        }

        val token = tokenStorage.accessToken
        if (token == null) {
            return chain.proceed(original)
        }

        val authorized = original.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authorized)
    }
}