package com.pelvictrainer.domain.auth

sealed class LoginResult {
    object Success : LoginResult()
    data class Requires2FA(val userId: Int, val email: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

data class TwoFASetupData(
    val secret: String,
    val qrCodeUrl: String,
)