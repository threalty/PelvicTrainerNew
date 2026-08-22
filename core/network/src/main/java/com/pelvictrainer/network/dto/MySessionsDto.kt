@file:OptIn(kotlinx.serialization.InternalSerializationApi::class, kotlinx.serialization.ExperimentalSerializationApi::class)

package com.pelvictrainer.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MySessionsResponseDto(
    val sessions: List<SessionDto>,
    val count: Int,
)

@Serializable
data class SessionDto(
    val id: Int,
    @SerialName("preset_id") val presetId: Int,
    @SerialName("preset_name") val presetName: String = "",
    @SerialName("completed_at") val completedAt: String,
    @SerialName("duration_seconds") val durationSeconds: Int,
    @SerialName("repeats_completed") val repeatsCompleted: Int,
)