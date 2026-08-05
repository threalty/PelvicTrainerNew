package com.pelvictrainer.database.di

import android.content.Context
import androidx.room.Room
import com.pelvictrainer.database.PelvicDatabase
import com.pelvictrainer.database.dao.TrainingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PelvicDatabase {
        return Room.databaseBuilder(
            context,
            PelvicDatabase::class.java,
            "pelvic_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePresetDao(database: PelvicDatabase): TrainingDao = database.trainingDao()

    @Provides
    @Singleton
    fun provideTrainingSessionDao(database: PelvicDatabase): TrainingDao = database.trainingDao()
}