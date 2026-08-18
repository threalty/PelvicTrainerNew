package com.pelvictrainer.domain.repository

import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.domain.model.TrainingSession
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    fun getPresets(): Flow<List<TrainingPreset>>
    suspend fun getPresetById(id: Long): TrainingPreset
    fun getSessions(): Flow<List<TrainingSession>>
    suspend fun saveSession(session: TrainingSession)
    suspend fun saveTrainingSession(presetId: Long, completedReps: Int, durationSeconds: Long)
    suspend fun deleteAllSessions()

    // ===== НОВОЕ: для синхронизации =====
    suspend fun getUnsyncedSessions(): List<TrainingSession>
    suspend fun markAsSynced(localId: Long, serverSessionId: Int)
    suspend fun hasDuplicateSession(date: Long, presetId: Long, duration: Long): Boolean
    suspend fun insertFromServer(session: TrainingSession)
    suspend fun deleteSyncedSessions()
}