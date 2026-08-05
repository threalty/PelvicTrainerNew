package com.pelvictrainer.domain.repository

import com.pelvictrainer.domain.model.TrainingPreset
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    /**
     * Получить список всех доступных пресетов
     */
    fun getPresets(): Flow<List<TrainingPreset>>

    /**
     * Получить конкретный пресет по ID
     */
    suspend fun getPresetById(id: Long): TrainingPreset

    /**
     * Начать тренировку с указанным пресетом
     */
    suspend fun startTraining(preset: TrainingPreset)

    /**
     * Завершить текущее повторение или фазу тренировки
     */
    suspend fun completeTraining(presetId: Long)

    /**
     * Сохранить результат тренировки (опционально, для статистики)
     */
    suspend fun saveTrainingSession(presetId: Long, completedReps: Int, durationSeconds: Long)
}