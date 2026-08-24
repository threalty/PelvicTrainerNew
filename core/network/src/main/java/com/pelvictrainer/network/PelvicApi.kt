@file:OptIn(kotlinx.serialization.InternalSerializationApi::class, kotlinx.serialization.ExperimentalSerializationApi::class)

package com.pelvictrainer.network

import com.pelvictrainer.network.dto.MySessionsResponseDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val consentPrivacy: Boolean,
    val consentHealth: Boolean,
    val consentAge: Boolean
)

@Serializable
data class UserDto(val id: Int, val email: String, val name: String)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    val user: UserDto,
)

@Serializable
data class RefreshRequest(@SerialName("refresh_token") val refreshToken: String)

@Serializable
data class RefreshResponse(@SerialName("access_token") val accessToken: String)

@Serializable
data class SessionLogRequest(
    @SerialName("preset_id") val presetId: Int,
    @SerialName("duration_seconds") val durationSeconds: Int,
    @SerialName("repeats_completed") val repeatsCompleted: Int,
)

@Serializable
data class SessionLogResponse(
    val message: String,
    @SerialName("session_id") val sessionId: Int,
    @SerialName("current_streak") val currentStreak: Int,
)

@Serializable
data class MyStatsDto(
    @SerialName("total_sessions") val totalSessions: Int,
    @SerialName("total_minutes") val totalMinutes: Int,
    @SerialName("current_streak") val currentStreak: Int,
    @SerialName("last_session_at") val lastSessionAt: String? = null,
)

@Serializable
data class MySubscriptionDto(
    @SerialName("has_subscription") val hasSubscription: Boolean,
    val plan: String,
    val status: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
)

@Serializable
data class PresetDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    val difficulty: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("exercises_count") val exercisesCount: Int,
)

@Serializable
data class PresetsResponse(val presets: List<PresetDto>, val count: Int)

@Serializable
data class DeviceRegisterRequest(
    @SerialName("fcm_token") val fcmToken: String,
    val platform: String = "android",
    @SerialName("app_version") val appVersion: String? = null,
)

@Serializable
data class CreatePaymentRequest(val plan: String)

@Serializable
data class CreatePaymentResponse(
    @SerialName("payment_id") val paymentId: Int,
    val status: String,
    val message: String,
)

@Serializable
data class PaymentDto(
    val id: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("amount_cents") val amountCents: Int,
    val currency: String,
    val plan: String,
    val status: String,
    val description: String? = null,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class MyPaymentsResponse(
    val payments: List<PaymentDto>,
    val count: Int,
)

// === НОВОЕ: Восстановление пароля ===

@Serializable
data class ForgotPasswordRequest(val email: String)

@Serializable
data class MessageResponse(val message: String)

interface PelvicApi {
    @POST("api/v1/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): RefreshResponse

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body body: RefreshRequest)

    @GET("api/v1/presets")
    suspend fun getPresets(): PresetsResponse

    @POST("api/v1/sessions")
    suspend fun logSession(@Body body: SessionLogRequest): SessionLogResponse

    @GET("api/v1/me/sessions")
    suspend fun getMySessions(): MySessionsResponseDto

    @GET("api/v1/me/stats")
    suspend fun getMyStats(): MyStatsDto

    @GET("api/v1/me/subscription")
    suspend fun getMySubscription(): MySubscriptionDto

    @POST("api/v1/devices")
    suspend fun registerDevice(@Body body: DeviceRegisterRequest)

    @POST("api/v1/payments/create")
    suspend fun createPayment(@Body body: CreatePaymentRequest): CreatePaymentResponse

    @GET("api/v1/me/payments")
    suspend fun getMyPayments(): MyPaymentsResponse

    // === НОВОЕ: Восстановление пароля ===

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): MessageResponse
}