package com.pelvictrainer.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pelvictrainer.database.entities.PresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {

    @Query("SELECT * FROM training_presets ORDER BY id ASC")
    fun getAllPresets(): Flow<List<PresetEntity>>

    @Query("SELECT * FROM training_presets WHERE id = :presetId")
    suspend fun getPresetById(presetId: Long): PresetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: PresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<PresetEntity>)

    @Query("DELETE FROM training_presets")
    suspend fun deleteAllPresets()

    @Query("SELECT COUNT(*) FROM training_presets")
    suspend fun getCount(): Int
}