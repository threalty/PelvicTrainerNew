package com.pelvictrainer.datastore.di


import android.content.Context


import androidx.datastore.core.DataStore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory

import androidx.datastore.preferences.core.Preferences

import androidx.datastore.preferences.preferencesDataStoreFile


import com.pelvictrainer.datastore.PelvicDataStore

import com.pelvictrainer.datastore.PreferencesDataStore


import dagger.Module

import dagger.Provides

import dagger.hilt.InstallIn

import dagger.hilt.android.qualifiers.ApplicationContext

import dagger.hilt.components.SingletonComponent


import javax.inject.Singleton



@Module

@InstallIn(SingletonComponent::class)

object DataStoreModule {



    private const val DATASTORE_NAME =
        "pelvictrainer_preferences"



    @Provides

    @Singleton

    fun provideDataStore(

        @ApplicationContext
        context: Context

    ): DataStore<Preferences> {


        return PreferenceDataStoreFactory.create {


            context.preferencesDataStoreFile(
                DATASTORE_NAME
            )


        }

    }




    @Provides

    @Singleton

    fun providePelvicDataStore(

        dataStore: DataStore<Preferences>

    ): PelvicDataStore {


        return PreferencesDataStore(
            dataStore
        )

    }


}