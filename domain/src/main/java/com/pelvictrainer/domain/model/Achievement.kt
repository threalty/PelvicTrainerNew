package com.pelvictrainer.domain.model

enum class AchievementType {
    FIRST_TRAINING,
    TRAINING_COUNT_10,
    TRAINING_COUNT_50,
    TRAINING_COUNT_100,
    STREAK_7_DAYS,
    STREAK_30_DAYS,
    STREAK_100_DAYS,
    TOTAL_TIME_1_HOUR
}

data class Achievement(
    val type: AchievementType,
    val title: String,
    val description: String,
    val icon: String, // emoji для простоты
    val targetValue: Int,
    val currentValue: Int,
    val isUnlocked: Boolean
) {
    val progress: Float
        get() = if (targetValue == 0) 0f else (currentValue.toFloat() / targetValue).coerceIn(0f, 1f)
}