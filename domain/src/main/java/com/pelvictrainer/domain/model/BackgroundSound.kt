package com.pelvictrainer.domain.model

enum class BackgroundSound(val displayName: String, val resourceId: String) {
    NONE("Нет звука", "none"),
    RAIN("Дождь", "rain"),
    FOREST("Лес", "forest"),
    OCEAN("Океан", "ocean"),
    BINAURAL("Бинауральные ритмы", "binaural")
}