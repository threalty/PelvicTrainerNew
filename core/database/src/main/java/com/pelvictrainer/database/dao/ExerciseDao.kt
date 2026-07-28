package com.pelvictrainer.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pelvictrainer.database.entities.ExerciseEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ExerciseDao {


    @Query(
        """
        SELECT * FROM exercises
        ORDER BY orderIndex ASC
        """
    )
    fun getExercises(): Flow<List<ExerciseEntity>>


    @Insert
    suspend fun insertAll(
        exercises: List<ExerciseEntity>
    )


    @Query(
        """
        DELETE FROM exercises
        """
    )
    suspend fun deleteAll()

}