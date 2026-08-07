package com.pelvictrainer.feature

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.pelvictrainer.domain.model.TrainingPhase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingHaptic @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var intensity: Float = 0.8f
    private var isEnabled: Boolean = true

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun setIntensity(value: Float) {
        intensity = value.coerceIn(0f, 1f)
    }

    @SuppressLint("MissingPermission")
    fun vibrateForPhase(phase: TrainingPhase) {
        if (!isEnabled || intensity <= 0f) return
        if (vibrator?.hasVibrator() != true) return

        val durationMs = when (phase) {
            TrainingPhase.SQUEEZE -> 150L
            TrainingPhase.HOLD -> 100L
            TrainingPhase.RELAX -> 200L
            TrainingPhase.FINISHED -> 400L
            else -> return
        }

        val amplitude = (intensity * 255).toInt().coerceIn(1, 255)
        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
    }
}