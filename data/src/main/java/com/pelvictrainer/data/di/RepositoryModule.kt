package com.pelvictrainer.data.di

import com.pelvictrainer.data.repository.TrainingRepositoryImpl
import com.pelvictrainer.data.repository.UserPreferencesRepositoryImpl
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTrainingRepository(
        implementation: TrainingRepositoryImpl
    ): TrainingRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        implementation: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}