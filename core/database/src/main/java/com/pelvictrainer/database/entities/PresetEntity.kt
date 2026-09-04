package com.pelvictrainer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_presets")
data class PresetEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String,
    val level: String, // BEGINNER, INTERMEDIATE, ADVANCED
    val squeezeTime: Int,
    val holdTime: Int,
    val relaxTime: Int,
    val totalReps: Int,
    val serverId: Int? = null
)