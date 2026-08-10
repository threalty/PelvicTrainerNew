package com.pelvictrainer.app.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "training_reminder_worker"
    }

    override suspend fun doWork(): Result {
        return try {
            val prefs = userPreferencesRepository.userPreferences.first()

            if (!prefs.remindersEnabled) return Result.success()
            if (prefs.reminderTimes.isEmpty()) return Result.success()

            val now = LocalDateTime.now()
            val currentDayOfWeek = now.dayOfWeek.value
            val currentTotalMinutes = now.hour * 60 + now.minute

            if (currentDayOfWeek !in prefs.reminderDaysOfWeek) {
                return Result.success()
            }

            for (reminder in prefs.reminderTimes) {
                val reminderTotalMinutes = reminder.hour * 60 + reminder.minute
                val diffMinutes = currentTotalMinutes - reminderTotalMinutes

                if (diffMinutes in 0..14) {
                    notificationHelper.showTrainingReminder(reminder.hour, reminder.minute)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}