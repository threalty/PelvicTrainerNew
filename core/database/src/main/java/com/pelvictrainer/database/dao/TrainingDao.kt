package com.pelvictrainer.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pelvictrainer.database.entities.TrainingSessionEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface TrainingDao {


    @Query(
        """
        SELECT * FROM training_sessions
        ORDER BY date DESC
        """
    )
    fun getSessions(): Flow<List<TrainingSessionEntity>>


    @Insert
    suspend fun insertSession(
        session: TrainingSessionEntity
    )


    @Query(
        """
        DELETE FROM training_sessions
        """
    )
    suspend fun deleteAll()


    @Query(
        """
        SELECT COUNT(*) FROM training_sessions
        """
    )
    fun getCount(): Flow<Int>

}