package com.pelvictrainer.feature


import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.annotation.SuppressLint



@SuppressLint("MissingPermission")
fun phaseVibration(
    context: Context
) {


    val vibrator =
        context.getSystemService(
            Vibrator::class.java
        )


    vibrator?.let {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            it.vibrate(
                VibrationEffect.createOneShot(
                    80,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )


        } else {


            @Suppress("DEPRECATION")
            it.vibrate(80)


        }


    }

}