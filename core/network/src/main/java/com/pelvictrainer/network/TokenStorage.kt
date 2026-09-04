package com.pelvictrainer.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Состояние авторизации пользователя.
 * Эмитится через authStateFlow при каждом изменении токенов.
 */
data class AuthState(
    val isLoggedIn: Boolean = false,
    val email: String? = null,
    val name: String? = null,
)

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            "pelvic_auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    // === Реактивное состояние авторизации ===
    private val _authStateFlow = MutableStateFlow(loadCurrentAuthState())
    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    private fun loadCurrentAuthState(): AuthState {
        return AuthState(
            isLoggedIn = prefs.getString(KEY_REFRESH, null) != null,
            email = prefs.getString(KEY_EMAIL, null),
            name = prefs.getString(KEY_NAME, null),
        )
    }

    private fun emitAuthState() {
        _authStateFlow.value = loadCurrentAuthState()
    }

    var accessToken: String?
        get() = prefs.getString(KEY_ACCESS, null)
        set(value) {
            prefs.edit { putString(KEY_ACCESS, value) }
            emitAuthState()
        }

    var refreshToken: String?
        get() = prefs.getString(KEY_REFRESH, null)
        set(value) {
            prefs.edit { putString(KEY_REFRESH, value) }
            emitAuthState()
        }

    var userEmail: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) {
            prefs.edit { putString(KEY_EMAIL, value) }
            emitAuthState()
        }

    var userName: String?
        get() = prefs.getString(KEY_NAME, null)
        set(value) {
            prefs.edit { putString(KEY_NAME, value) }
            emitAuthState()
        }

    val isLoggedIn: Boolean
        get() = refreshToken != null

    fun clear() {
        prefs.edit { clear() }
        emitAuthState()
    }

    private companion object {
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_EMAIL = "user_email"
        const val KEY_NAME = "user_name"
    }
}