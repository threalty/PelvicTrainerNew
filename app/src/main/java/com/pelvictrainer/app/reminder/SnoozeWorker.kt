package com.pelvictrainer.app.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SnoozeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_HOUR = "key_hour"
        const val KEY_MINUTE = "key_minute"
    }

    override suspend fun doWork(): Result {
        return try {
            val hour = inputData.getInt(KEY_HOUR, 0)
            val minute = inputData.getInt(KEY_MINUTE, 0)
            notificationHelper.showTrainingReminder(hour, minute)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}