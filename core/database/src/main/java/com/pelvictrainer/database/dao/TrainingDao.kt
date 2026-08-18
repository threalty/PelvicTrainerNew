package com.pelvictrainer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pelvictrainer.database.entities.TrainingSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {

    @Query("SELECT * FROM training_sessions ORDER BY date DESC")
    fun getSessions(): Flow<List<TrainingSessionEntity>>

    @Insert
    suspend fun insertSession(session: TrainingSessionEntity): Long

    @Query("DELETE FROM training_sessions")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM training_sessions")
    fun getCount(): Flow<Int>

    // ===== НОВОЕ: для синхронизации =====

    @Query("SELECT * FROM training_sessions WHERE synced = 0 ORDER BY date ASC")
    suspend fun getUnsyncedSessions(): List<TrainingSessionEntity>

    @Query("UPDATE training_sessions SET synced = 1, serverSessionId = :serverId WHERE id = :localId")
    suspend fun markAsSynced(localId: Long, serverId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM training_sessions WHERE serverSessionId = :serverId)")
    suspend fun hasSessionWithServerId(serverId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM training_sessions WHERE date = :date AND presetId = :presetId AND durationSeconds = :duration)")
    suspend fun hasDuplicateSession(date: Long, presetId: Long, duration: Long): Boolean

    @Query("DELETE FROM training_sessions WHERE synced = 1")
    suspend fun deleteSyncedSessions()
}