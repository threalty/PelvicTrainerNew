package com.pelvictrainer.data.repository

import android.util.Log
import com.pelvictrainer.database.dao.PresetDao
import com.pelvictrainer.database.dao.TrainingDao
import com.pelvictrainer.database.entities.PresetEntity
import com.pelvictrainer.data.mapper.toDomain
import com.pelvictrainer.data.mapper.toEntity
import com.pelvictrainer.domain.model.DefaultTrainingPresets
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.model.TrainingPreset
import com.pelvictrainer.domain.model.TrainingSession
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.network.PelvicApi
import com.pelvictrainer.network.PresetDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingRepositoryImpl @Inject constructor(
    private val dao: TrainingDao,
    private val presetDao: PresetDao,
    private val api: PelvicApi
) : TrainingRepository {

    companion object {
        private const val TAG = "TrainingRepo"
    }

    override fun getPresets(): Flow<List<TrainingPreset>> =
        presetDao.getAllPresets().map { entities ->
            if (entities.isEmpty()) {
                // Fallback на hardcoded если БД пуста
                DefaultTrainingPresets.getAll()
            } else {
                entities.map { it.toDomain() }
            }
        }

    override suspend fun getPresetById(id: Long): TrainingPreset {
        val entity = presetDao.getPresetById(id)
        return entity?.toDomain() ?: when (id) {
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
                synced = false,
                serverSessionId = null,
            )
        )
    }

    override suspend fun deleteAllSessions() = dao.deleteAll()

    // ===== Синхронизация сессий =====

    override suspend fun getUnsyncedSessions(): List<TrainingSession> =
        dao.getUnsyncedSessions().map { it.toDomain() }

    override suspend fun markAsSynced(localId: Long, serverSessionId: Int) {
        dao.markAsSynced(localId, serverSessionId)
    }

    override suspend fun hasDuplicateSession(date: Long, presetId: Long, duration: Long): Boolean =
        dao.hasDuplicateSession(date, presetId, duration)

    override suspend fun insertFromServer(session: TrainingSession) {
        dao.insertSession(session.toEntity())
    }

    override suspend fun deleteSyncedSessions() = dao.deleteSyncedSessions()

    // ===== НОВОЕ: Синхронизация пресетов =====

    override suspend fun refreshPresetsFromServer(): Result<Unit> {
        return try {
            Log.d(TAG, "🔄 Загружаем пресеты с сервера...")
            val response = api.getPresets()
            Log.d(TAG, "✅ Получено ${response.presets.size} пресетов с сервера")

            val presetEntities = response.presets.map { dto ->
                dto.toPresetEntity()
            }

            // Очищаем старые пресеты и сохраняем новые
            presetDao.deleteAllPresets()
            presetDao.insertPresets(presetEntities)

            Log.d(TAG, "✅ Пресеты сохранены в БД: ${presetEntities.size} штук")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Не удалось загрузить пресеты с сервера: ${e.message}")
            Log.d(TAG, "Используем локальные пресеты из БД или hardcoded")
            Result.failure(e)
        }
    }
}

// === Extension функция для конвертации DTO → Entity ===
private fun PresetDto.toPresetEntity(): PresetEntity {
    val (level, squeezeTime, holdTime, relaxTime, totalReps) = getPhaseDetailsForDifficulty(difficulty)

    return PresetEntity(
        id = id.toLong(),
        name = name,
        description = description ?: "Программа тренировок",
        level = level.name,
        squeezeTime = squeezeTime,
        holdTime = holdTime,
        relaxTime = relaxTime,
        totalReps = totalReps,
        serverId = id
    )
}

// === Маппинг difficulty → параметры фаз ===
private fun getPhaseDetailsForDifficulty(difficulty: String): PhaseDetails {
    return when (difficulty.lowercase()) {
        "beginner" -> PhaseDetails(
            level = TrainingLevel.BEGINNER,
            squeezeTime = 3,
            holdTime = 3,
            relaxTime = 5,
            totalReps = 10
        )
        "intermediate" -> PhaseDetails(
            level = TrainingLevel.INTERMEDIATE,
            squeezeTime = 5,
            holdTime = 5,
            relaxTime = 5,
            totalReps = 15
        )
        "advanced" -> PhaseDetails(
            level = TrainingLevel.ADVANCED,
            squeezeTime = 8,
            holdTime = 8,
            relaxTime = 4,
            totalReps = 20
        )
        else -> PhaseDetails(
            level = TrainingLevel.BEGINNER,
            squeezeTime = 3,
            holdTime = 3,
            relaxTime = 5,
            totalReps = 10
        )
    }
}

private data class PhaseDetails(
    val level: TrainingLevel,
    val squeezeTime: Int,
    val holdTime: Int,
    val relaxTime: Int,
    val totalReps: Int
)

// === Extension функция для конвертации Entity → Domain ===
private fun PresetEntity.toDomain(): TrainingPreset {
    return TrainingPreset(
        id = id,
        name = name,
        description = description,
        level = when (level.uppercase()) {
            "BEGINNER" -> TrainingLevel.BEGINNER
            "INTERMEDIATE" -> TrainingLevel.INTERMEDIATE
            "ADVANCED" -> TrainingLevel.ADVANCED
            else -> TrainingLevel.BEGINNER
        },
        squeezeTime = squeezeTime,
        holdTime = holdTime,
        relaxTime = relaxTime,
        totalReps = totalReps
    )
}