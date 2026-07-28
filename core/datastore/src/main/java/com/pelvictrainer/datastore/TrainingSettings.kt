package com.pelvictrainer.datastore


data class TrainingSettings(

    val dailyGoalMinutes: Int,

    val reminderEnabled: Boolean,

    val reminderHour: Int,

    val reminderMinute: Int

)