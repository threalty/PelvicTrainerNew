package com.pelvictrainer.app.analytics

import com.pelvictrainer.domain.analytics.AnalyticsTracker
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AnalyticsEntryPoint {
    fun analyticsTracker(): AnalyticsTracker
}