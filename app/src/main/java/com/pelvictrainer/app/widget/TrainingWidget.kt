package com.pelvictrainer.app.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.pelvictrainer.app.MainActivity
import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.model.UserPreferences
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TrainingWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )
        val trainingRepository = entryPoint.trainingRepository()
        val prefsRepository = entryPoint.userPreferencesRepository()

        val sessions = trainingRepository.getSessions().first()
        val prefs = prefsRepository.userPreferences.first()

        val widgetData = buildWidgetData(sessions, prefs)

        provideContent {
            GlanceTheme {
                WidgetContent(data = widgetData)
            }
        }
    }

    private fun buildWidgetData(
        sessions: List<TrainingSession>,
        prefs: UserPreferences
    ): WidgetData {
        val now = LocalDate.now()
        val weekAgo = now.minusDays(6)
        val weekStartMillis = weekAgo.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val sessionsThisWeek = sessions.filter { it.date >= weekStartMillis }
        val completedThisWeek = sessionsThisWeek.size
        val weeklyGoal = prefs.weeklyGoal
        val progress = if (weeklyGoal > 0) {
            (completedThisWeek.toFloat() / weeklyGoal).coerceIn(0f, 1f)
        } else 0f

        val streak = calculateStreak(sessions)

        return WidgetData(
            currentStreak = streak,
            completedThisWeek = completedThisWeek,
            weeklyGoal = weeklyGoal,
            progress = progress,
            totalTrainings = sessions.size
        )
    }

    private fun calculateStreak(sessions: List<TrainingSession>): Int {
        if (sessions.isEmpty()) return 0

        val trainingDates = sessions.map { session ->
            Instant.ofEpochMilli(session.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.distinct().sortedDescending()

        if (trainingDates.isEmpty()) return 0

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        if (trainingDates.first() != today && trainingDates.first() != yesterday) {
            return 0
        }

        var streak = 1
        for (i in 1 until trainingDates.size) {
            val diff = ChronoUnit.DAYS.between(trainingDates[i], trainingDates[i - 1])
            if (diff == 1L) {
                streak++
            } else {
                break
            }
        }

        return streak
    }
}

data class WidgetData(
    val currentStreak: Int,
    val completedThisWeek: Int,
    val weeklyGoal: Int,
    val progress: Float,
    val totalTrainings: Int
)

private val Bordeaux = Color(0xFFBE1D2C)
private val BordeauxDark = Color(0xFF930017)
private val WidgetBackground = Color(0xFF14181C)
private val WidgetSurface = Color(0xFF1E2227)
private val WidgetOnSurface = Color(0xFFE2E2E7)
private val WidgetOnSurfaceVariant = Color(0xFFA1A6AD)

@SuppressLint("RestrictedApi")
@Composable
private fun WidgetContent(data: WidgetData) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(WidgetBackground))
            .cornerRadius(16.dp)
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize()
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "🔥 ${data.currentStreak}",
                    style = TextStyle(
                        color = ColorProvider(WidgetOnSurface),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.width(8.dp))
                Text(
                    text = pluralizeDays(data.currentStreak),
                    style = TextStyle(
                        color = ColorProvider(WidgetOnSurfaceVariant),
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = "❤ PelvicTrainer",
                    style = TextStyle(
                        color = ColorProvider(Bordeaux),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            Text(
                text = "Недельная цель",
                style = TextStyle(
                    color = ColorProvider(WidgetOnSurfaceVariant),
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = "${data.completedThisWeek} / ${data.weeklyGoal}",
                style = TextStyle(
                    color = ColorProvider(WidgetOnSurface),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Прогресс-бар через вложенный Box с пустым content
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(ColorProvider(WidgetSurface))
                    .cornerRadius(3.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Box(
                    modifier = GlanceModifier
                        .width((data.progress * 300).dp.coerceAtLeast(1.dp))
                        .height(6.dp)
                        .background(ColorProvider(Bordeaux))
                        .cornerRadius(3.dp)
                ) {
                    // Пустой content для заполнения прогресс-бара
                }
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(ColorProvider(Bordeaux))
                    .cornerRadius(12.dp)
                    .clickable(actionRunCallback<StartTrainingAction>())
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶ Начать тренировку",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

private fun pluralizeDays(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    return when {
        mod10 == 1 && mod100 != 11 -> "день"
        mod10 in 2..4 && mod100 !in 12..14 -> "дня"
        else -> "дней"
    }
}

class StartTrainingAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "training")
        }
        context.startActivity(intent)
    }
}