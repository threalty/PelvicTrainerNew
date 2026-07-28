package com.pelvictrainer.database.entities


import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(
    tableName = "training_sessions"
)
data class TrainingSessionEntity(


    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,


    val date: Long,


    val durationSeconds: Int,


    val repeats: Int,


    val presetId: String

)