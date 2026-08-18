package com.pelvictrainer.data.di

import com.pelvictrainer.data.subscription.SubscriptionRepositoryImpl
import com.pelvictrainer.domain.subscription.SubscriptionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SubscriptionDataModule {
    @Binds
    abstract fun bindSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository
}