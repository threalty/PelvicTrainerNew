package com.pelvictrainer.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercises"
)
data class ExerciseEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val description: String,

    val contractSeconds: Int,

    val holdSeconds: Int,

    val relaxSeconds: Int,

    val repeats: Int,

    val orderIndex: Int
)