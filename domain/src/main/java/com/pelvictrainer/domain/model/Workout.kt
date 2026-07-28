package com.pelvictrainer.domain.model

data class Workout(

    val id: String,

    val title: String,

    val description: String,

    val exercises: List<Exercise>

)