package com.pelvictrainer.designsystem.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class HapticHelper(private val context: Context) {

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun lightTap() {
        vibrate(10L, 50)
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun mediumTap() {
        vibrate(20L, 100)
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun error() {
        vibratePattern(longArrayOf(0, 50, 50, 50), intArrayOf(0, 150, 0, 150))
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    private fun vibrate(durationMs: Long, amplitude: Int) {
        try {
            val vibrator = getVibrator() ?: return
            // minSdk 26 (Oreo), поэтому VibrationEffect доступен всегда без проверок
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
        } catch (e: Exception) {
            // Игнорируем ошибки вибрации
        }
    }

    @RequiresPermission(android.Manifest.permission.VIBRATE)
    private fun vibratePattern(timings: LongArray, amplitudes: IntArray) {
        try {
            val vibrator = getVibrator() ?: return
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } catch (e: Exception) {
            // Игнорируем ошибки вибрации
        }
    }

    private fun getVibrator(): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}

@Composable
fun rememberHapticHelper(): HapticHelper {
    val context = LocalContext.current
    return remember { HapticHelper(context) }
}