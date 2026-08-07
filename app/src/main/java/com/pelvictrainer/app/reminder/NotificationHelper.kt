package com.pelvictrainer.app.reminder

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
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "training")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            hour * 60 + minute, // уникальный requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Пора тренироваться! 🏋️")
            .setContentText("Всего 5 минут для вашего здоровья. Начните тренировку прямо сейчас!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Всего 5 минут для вашего здоровья. Начните тренировку прямо сейчас!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationId = NOTIFICATION_ID_BASE + hour * 60 + minute
        notificationManager.notify(notificationId, notification)
    }
}