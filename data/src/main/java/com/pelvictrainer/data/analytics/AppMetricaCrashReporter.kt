package com.pelvictrainer.data.analytics

import android.util.Log
import com.pelvictrainer.domain.analytics.CrashReporter
import com.yandex.metrica.YandexMetrica
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetricaCrashReporter @Inject constructor() : CrashReporter {

    companion object {
        private const val TAG = "AppMetricaCrash"
    }

    override fun logException(throwable: Throwable) {
        try {
            YandexMetrica.reportError(
                throwable.message ?: "Unknown error",
                throwable
            )
            Log.w(TAG, "Exception reported: ${throwable.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error reporting exception: ${e.message}")
        }
    }

    override fun log(message: String) {
        try {
            YandexMetrica.reportEvent("app_log", mapOf("message" to message))
        } catch (e: Exception) {
            Log.e(TAG, "Error logging: ${e.message}")
        }
    }

    override fun setUserId(userId: String) {
        try {
            YandexMetrica.setUserProfileID(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting user ID: ${e.message}")
        }
    }

    override fun setUserAttribute(key: String, value: String) {
        try {
            YandexMetrica.reportEvent("user_attribute", mapOf(key to value))
        } catch (e: Exception) {
            Log.e(TAG, "Error setting attribute: ${e.message}")
        }
    }
}