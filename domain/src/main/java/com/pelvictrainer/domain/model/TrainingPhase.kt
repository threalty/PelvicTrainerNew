package com.pelvictrainer.domain.model

enum class TrainingPhase {
    IDLE,       // Ожидание начала
    SQUEEZE,    // Фаза сжатия
    HOLD,       // Фаза удержания
    RELAX,      // Фаза расслабления
    FINISHED    // Тренировка завершена
}