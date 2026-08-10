package com.pelvictrainer.data.analytics

import android.util.Log
import com.pelvictrainer.domain.analytics.AnalyticsTracker
import com.pelvictrainer.domain.analytics.CrashReporter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpAnalytics @Inject constructor() : AnalyticsTracker {
    override fun trackEvent(name: String, params: Map<String, Any>) {
        Log.d("NoOpAnalytics", "Event: $name, params: $params")
    }

    override fun trackScreen(screenName: String) {
        Log.d("NoOpAnalytics", "Screen: $screenName")
    }

    override fun setUserId(userId: String) {
        Log.d("NoOpAnalytics", "UserId: $userId")
    }

    override fun setUserAttribute(key: String, value: String) {
        Log.d("NoOpAnalytics", "Attr: $key=$value")
    }
}

@Singleton
class NoOpCrashReporter @Inject constructor() : CrashReporter {
    override fun logException(throwable: Throwable) {
        Log.w("NoOpCrashReporter", "Exception: ${throwable.message}")
    }

    override fun log(message: String) {
        Log.d("NoOpCrashReporter", "Log: $message")
    }

    override fun setUserId(userId: String) {
        Log.d("NoOpCrashReporter", "UserId: $userId")
    }

    override fun setUserAttribute(key: String, value: String) {
        Log.d("NoOpCrashReporter", "Attr: $key=$value")
    }
}