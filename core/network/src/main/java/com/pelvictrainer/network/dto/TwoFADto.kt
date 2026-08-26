package com.pelvictrainer.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifySetupRequest(val secret: String, val code: String)

@Serializable
data class VerifyLoginRequest(
    @SerialName("user_id") val userId: Int,
    val code: String,
)

@Serializable
data class VerifyBackupRequest(
    @SerialName("user_id") val userId: Int,
    val code: String,
)

@Serializable
data class TwoFADisableRequest(val code: String)

@Serializable
data class TwoFAStatusResponse(val enabled: Boolean)

@Serializable
data class TwoFASetupResponse(
    val secret: String,
    @SerialName("qr_code_url") val qrCodeUrl: String,
)

@Serializable
data class TwoFASetupCompleteResponse(
    val message: String,
    @SerialName("backup_codes") val backupCodes: List<String>,
)

@Serializable
data class TwoFAVerifyLoginResponse(
    val message: String? = null,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user_id") val userId: Int,
    val email: String,
    val name: String? = null,
    val authenticated: Boolean,
)

@Serializable
data class TwoFAVerifyBackupResponse(
    val message: String? = null,
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user_id") val userId: Int,
    val email: String,
    val name: String? = null,
    @SerialName("remaining_backup_codes") val remainingBackupCodes: Int,
    val authenticated: Boolean,
)