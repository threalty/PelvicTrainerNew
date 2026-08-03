package com.pelvictrainer.domain.model



data class TrainingPreset(

    val id: String,

    val name: String,

    val description: String,

    val contractSeconds: Int,

    val holdSeconds: Int,

    val relaxSeconds: Int,

    val repeats: Int

)