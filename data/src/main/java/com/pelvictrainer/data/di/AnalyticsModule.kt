package com.pelvictrainer.data.di

import com.pelvictrainer.data.analytics.AppMetricaAnalytics
import com.pelvictrainer.data.analytics.AppMetricaCrashReporter
import com.pelvictrainer.domain.analytics.AnalyticsTracker
import com.pelvictrainer.domain.analytics.CrashReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(
        impl: AppMetricaAnalytics
    ): AnalyticsTracker

    @Binds
    @Singleton
    abstract fun bindCrashReporter(
        impl: AppMetricaCrashReporter
    ): CrashReporter
}