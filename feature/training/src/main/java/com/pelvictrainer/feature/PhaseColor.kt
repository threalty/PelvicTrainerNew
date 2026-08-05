package com.pelvictrainer.feature

import androidx.compose.ui.graphics.Color
import com.pelvictrainer.domain.model.TrainingPhase

fun phaseColor(phase: TrainingPhase): Color {
    return when (phase) {
        TrainingPhase.IDLE -> Color.Gray
        TrainingPhase.SQUEEZE -> Color.Red
        TrainingPhase.HOLD -> Color(0xFFFF9800)
        TrainingPhase.RELAX -> Color(0xFF4CAF50)
        TrainingPhase.FINISHED -> Color(0xFF2196F3)
    }
}