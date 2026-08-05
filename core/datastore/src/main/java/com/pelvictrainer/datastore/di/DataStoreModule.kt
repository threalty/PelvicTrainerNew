package com.pelvictrainer.datastore.di

import com.pelvictrainer.datastore.PelvicDataStore
import com.pelvictrainer.datastore.PreferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindPelvicDataStore(
        impl: PreferencesDataStore
    ): PelvicDataStore
}