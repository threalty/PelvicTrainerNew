package com.pelvictrainer.app.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pelvictrainer.app.analytics.AnalyticsEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.util.concurrent.TimeUnit

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_HOUR = "extra_hour"
        const val EXTRA_MINUTE = "extra_minute"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val hour = intent.getIntExtra(EXTRA_HOUR, 0)
        val minute = intent.getIntExtra(EXTRA_MINUTE, 0)

        when (intent.action) {
            NotificationHelper.ACTION_SNOOZE_5 -> {
                scheduleSnooze(context, hour, minute, 5)
            }
            NotificationHelper.ACTION_SNOOZE_15 -> {
                scheduleSnooze(context, hour, minute, 15)
            }
        }
    }

    private fun scheduleSnooze(
        context: Context,
        hour: Int,
        minute: Int,
        delayMinutes: Long
    ) {
        // Логируем snooze перед планированием
        try {
            val analytics = EntryPointAccessors.fromApplication(
                context.applicationContext,
                AnalyticsEntryPoint::class.java
            ).analyticsTracker()

            analytics.trackEvent(
                "reminder_snoozed",
                mapOf(
                    "snooze_minutes" to delayMinutes,
                    "original_hour" to hour,
                    "original_minute" to minute
                )
            )
        } catch (e: Exception) {
            // Игнорируем ошибки аналитики
        }

        val inputData = Data.Builder()
            .putInt(SnoozeWorker.KEY_HOUR, hour)
            .putInt(SnoozeWorker.KEY_MINUTE, minute)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SnoozeWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}