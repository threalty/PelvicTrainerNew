package com.pelvictrainer.domain.model

enum class TrainingLevel(val displayName: String) {
    BEGINNER("Начинающий"),
    INTERMEDIATE("Средний"),
    ADVANCED("Продвинутый");

    companion object {
        fun fromOrdinal(ordinal: Int): TrainingLevel = entries.getOrElse(ordinal) { BEGINNER }
    }
}