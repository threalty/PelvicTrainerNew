package com.pelvictrainer.feature


import com.pelvictrainer.domain.model.TrainingPreset


data class TrainingSettingsState(

    val presets: List<TrainingPreset> = emptyList(),

    val selectedPreset: TrainingPreset? = null

)