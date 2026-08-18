package com.pelvictrainer.data.repository

import com.pelvictrainer.data.mapper.toDomain
import com.pelvictrainer.data.mapper.toEntity
import com.pelvictrainer.database.dao.TrainingDao
import com.pelvictrainer.domain.model.DefaultTrainingPresets
import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingRepositoryImpl @Inject constructor(
    private val dao: TrainingDao
) : TrainingRepository {

    override fun getPresets(): Flow<List<TrainingPreset>> = flow {
        emit(DefaultTrainingPresets.getAll())
    }

    override suspend fun getPresetById(id: Long): TrainingPreset {
        return when (id) {
            1L -> DefaultTrainingPresets.getBeginner()
            2L -> DefaultTrainingPresets.getIntermediate()
            3L -> DefaultTrainingPresets.getAdvanced()
            else -> throw IllegalArgumentException("Preset $id not found")
        }
    }

    override fun getSessions(): Flow<List<TrainingSession>> =
        dao.getSessions().map { list -> list.map { it.toDomain() } }

    override suspend fun saveSession(session: TrainingSession) {
        dao.insertSession(session.toEntity())
    }

    override suspend fun saveTrainingSession(
        presetId: Long,
        completedReps: Int,
        durationSeconds: Long
    ) {
        saveSession(
            TrainingSession(
                id = 0L,
                presetId = presetId,
                date = System.currentTimeMillis(),
                durationSeconds = durationSeconds,
                repeats = completedReps,
                synced = false,  // ← НОВОЕ: помечаем как не отправленное на сервер
                serverSessionId = null,
            )
        )
    }

    override suspend fun deleteAllSessions() = dao.deleteAll()

    // ===== НОВОЕ: методы для синхронизации =====

    override suspend fun getUnsyncedSessions(): List<TrainingSession> =
        dao.getUnsyncedSessions().map { it.toDomain() }

    override suspend fun markAsSynced(localId: Long, serverSessionId: Int) {
        dao.markAsSynced(localId, serverSessionId)
    }

    override suspend fun hasDuplicateSession(date: Long, presetId: Long, duration: Long): Boolean =
        dao.hasDuplicateSession(date, presetId, duration)

    override suspend fun insertFromServer(session: TrainingSession) {
        // При загрузке с сервера — synced = true, чтобы не отправлять обратно
        dao.insertSession(session.toEntity())
    }

    override suspend fun deleteSyncedSessions() = dao.deleteSyncedSessions()
}