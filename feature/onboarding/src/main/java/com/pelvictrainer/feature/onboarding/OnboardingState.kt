package com.pelvictrainer.feature.onboarding

import com.pelvictrainer.domain.model.TrainingLevel

data class OnboardingState(
    val selectedLevel: TrainingLevel? = null,
    val isCompleted: Boolean = false
)