package com.pelvictrainer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pelvictrainer.database.dao.TrainingDao
import com.pelvictrainer.database.entities.TrainingSessionEntity

@Database(
    entities = [
        TrainingSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PelvicDatabase : RoomDatabase() {
    abstract fun trainingDao(): TrainingDao
}