package com.pelvictrainer.app.reminder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.pelvictrainer.app.MainActivity
import com.pelvictrainer.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "training_reminders"
        const val CHANNEL_NAME = "Напоминания о тренировках"
        private const val NOTIFICATION_ID_BASE = 1000

        const val ACTION_SNOOZE_5 = "com.pelvictrainer.SNOOZE_5"
        const val ACTION_SNOOZE_15 = "com.pelvictrainer.SNOOZE_15"

        const val REQUEST_OPEN_APP = 1000
        const val REQUEST_SNOOZE_5 = 1001
        const val REQUEST_SNOOZE_15 = 1002
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Ежедневные напоминания о тренировках"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showTrainingReminder(hour: Int, minute: Int) {
        val notificationId = NOTIFICATION_ID_BASE + hour * 60 + minute
        val notification = buildReminderNotification(hour, minute)
        notificationManager.notify(notificationId, notification)
    }

    fun buildReminderNotification(hour: Int, minute: Int): Notification {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "training")
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_OPEN_APP,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snooze5Intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE_5
            putExtra(NotificationActionReceiver.EXTRA_HOUR, hour)
            putExtra(NotificationActionReceiver.EXTRA_MINUTE, minute)
        }
        val snooze5PendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_SNOOZE_5,
            snooze5Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val snooze15Intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE_15
            putExtra(NotificationActionReceiver.EXTRA_HOUR, hour)
            putExtra(NotificationActionReceiver.EXTRA_MINUTE, minute)
        }
        val snooze15PendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_SNOOZE_15,
            snooze15Intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Пора тренироваться! 🏋️")
            .setContentText("Всего 5 минут для вашего здоровья. Начните прямо сейчас!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Всего 5 минут для вашего здоровья. Начните тренировку прямо сейчас!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(R.drawable.ic_notification, "Начать", openPendingIntent)
            .addAction(R.drawable.ic_notification, "+5 мин", snooze5PendingIntent)
            .addAction(R.drawable.ic_notification, "+15 мин", snooze15PendingIntent)
            .build()
    }

    fun cancelNotification(hour: Int, minute: Int) {
        val notificationId = NOTIFICATION_ID_BASE + hour * 60 + minute
        notificationManager.cancel(notificationId)
    }
}