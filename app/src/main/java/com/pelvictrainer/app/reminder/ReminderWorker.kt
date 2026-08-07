package com.pelvictrainer.app.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.LocalDate

/**
 * Worker, который запускается периодически (каждые 15 минут — минимум для WorkManager).
 * Проверяет, нужно ли показать напоминание в текущее время.
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "training_reminder_worker"
        const val LAST_SHOWN_DATE_KEY = "last_shown_date"
        const val LAST_SHOWN_TIME_KEY = "last_shown_time"
    }

    override suspend fun doWork(): Result {
        try {
            val prefs = userPreferencesRepository.userPreferences.first()

            // Если напоминания выключены — выходим
            if (!prefs.remindersEnabled) return Result.success()
            if (prefs.reminderTimes.isEmpty()) return Result.success()

            val now = LocalDateTime.now()
            val currentDayOfWeek = now.dayOfWeek.value // 1 (Пн) .. 7 (Вс)
            val currentHour = now.hour
            val currentMinute = now.minute

            // Проверяем, входит ли сегодня в выбранные дни
            if (currentDayOfWeek !in prefs.reminderDaysOfWeek) {
                return Result.success()
            }

            // Проверяем каждое настроенное время
            for (reminder in prefs.reminderTimes) {
                // Напоминание срабатывает, если текущее время в пределах 15 минут после заданного
                val diffMinutes = (currentHour * 60 + currentMinute) - (reminder.hour * 60 + reminder.minute)

                if (diffMinutes in 0..14) {
                    // Проверяем, не показывали ли уже это напоминание сегодня
                    val today = LocalDate.now().toString()
                    val shownKey = "${reminder.hour}_${reminder.minute}"

                    val lastShownDate = inputData.getString(LAST_SHOWN_DATE_KEY)
                    val lastShownTime = inputData.getString(LAST_SHOWN_TIME_KEY)

                    // Показываем уведомление (WorkManager перезапускает worker каждый день)
                    if (lastShownDate != today || lastShownTime != shownKey) {
                        notificationHelper.showTrainingReminder(reminder.hour, reminder.minute)
                    }
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}