package com.pelvictrainer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pelvictrainer.database.dao.PresetDao
import com.pelvictrainer.database.dao.TrainingDao
import com.pelvictrainer.database.entities.PresetEntity
import com.pelvictrainer.database.entities.TrainingSessionEntity

@Database(
    entities = [
        TrainingSessionEntity::class,
        PresetEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class PelvicDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
    abstract fun presetDao(): PresetDao
}