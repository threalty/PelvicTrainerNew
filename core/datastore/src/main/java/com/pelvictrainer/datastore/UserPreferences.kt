package com.pelvictrainer.datastore


data class UserPreferences(

    val isOnboardingCompleted: Boolean,

    val userAge: Int?,

    val trainingLevel: TrainingLevel

)



enum class TrainingLevel {

    BEGINNER,

    INTERMEDIATE,

    ADVANCED

}