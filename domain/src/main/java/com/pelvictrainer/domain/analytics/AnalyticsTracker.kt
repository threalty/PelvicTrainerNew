package com.pelvictrainer.domain.analytics

interface AnalyticsTracker {
    fun trackEvent(name: String, params: Map<String, Any> = emptyMap())
    fun trackScreen(screenName: String)
    fun setUserId(userId: String)
    fun setUserAttribute(key: String, value: String)
}

object AnalyticsEvents {
    // Тренировки
    const val TRAINING_STARTED = "training_started"
    const val TRAINING_COMPLETED = "training_completed"
    const val TRAINING_ABORTED = "training_aborted"

    // Уровни
    const val LEVEL_UP = "level_up"
    const val LEVEL_SELECTED = "level_selected"

    // Цели
    const val GOAL_REACHED = "goal_reached"
    const val GOAL_UPDATED = "goal_updated"

    // Напоминания
    const val REMINDER_ENABLED = "reminder_enabled"
    const val REMINDER_DISABLED = "reminder_disabled"
    const val REMINDER_SNOOZED = "reminder_snoozed"

    // Настройки
    const val THEME_CHANGED = "theme_changed"
    const val ACCENT_CHANGED = "accent_changed"
    const val SOUND_CHANGED = "sound_changed"
    const val VOICE_TOGGLED = "voice_toggled"
    const val VIBRATION_TOGGLED = "vibration_toggled"

    // Виджет
    const val WIDGET_ADDED = "widget_added"
    const val WIDGET_CLICKED = "widget_clicked"

    // Приложение
    const val APP_OPENED = "app_opened"
    const val ONBOARDING_COMPLETED = "onboarding_completed"
    const val WEEKLY_GOAL_REACHED = "weekly_goal_reached"
}

object AnalyticsParams {
    const val LEVEL = "level"
    const val PRESET_ID = "preset_id"
    const val DURATION = "duration_seconds"
    const val REPS = "reps"
    const val THEME_MODE = "theme_mode"
    const val ACCENT_COLOR = "accent_color"
    const val SOUND_TYPE = "sound_type"
    const val GOAL_VALUE = "goal_value"
    const val SNOOZE_MINUTES = "snooze_minutes"
    const val TRAINING_LEVEL = "training_level"
    const val SCREEN_NAME = "screen_name"
}