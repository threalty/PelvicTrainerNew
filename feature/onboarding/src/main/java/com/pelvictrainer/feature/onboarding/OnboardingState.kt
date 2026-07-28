package com.pelvictrainer.feature.onboarding


import com.pelvictrainer.datastore.TrainingLevel



data class OnboardingState(

    val selectedLevel: TrainingLevel =
        TrainingLevel.BEGINNER,


    val completed: Boolean = false,


    val loading: Boolean = false

)