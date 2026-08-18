package com.pelvictrainer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_sessions")
data class TrainingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val presetId: Long,
    val date: Long,
    val durationSeconds: Long,
    val repeats: Int,
    val synced: Boolean = false,
    val serverSessionId: Int? = null,
)