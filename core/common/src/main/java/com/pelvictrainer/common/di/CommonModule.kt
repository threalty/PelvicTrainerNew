package com.pelvictrainer.common.di


import com.pelvictrainer.common.DateTimeProvider

import com.pelvictrainer.common.DefaultDateTimeProvider

import com.pelvictrainer.common.DefaultDispatcherProvider

import com.pelvictrainer.common.DispatcherProvider


import dagger.Module

import dagger.Provides

import dagger.hilt.InstallIn

import dagger.hilt.components.SingletonComponent


import javax.inject.Singleton



@Module

@InstallIn(SingletonComponent::class)

object CommonModule {



    @Provides

    @Singleton

    fun provideDispatcherProvider():

            DispatcherProvider {

        return DefaultDispatcherProvider()

    }



    @Provides

    @Singleton

    fun provideDateTimeProvider():

            DateTimeProvider {

        return DefaultDateTimeProvider()

    }


}