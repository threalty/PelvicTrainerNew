package com.pelvictrainer.data.analytics

import android.util.Log
import com.pelvictrainer.domain.analytics.AnalyticsTracker
import com.yandex.metrica.YandexMetrica
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppMetricaAnalytics @Inject constructor() : AnalyticsTracker {

    companion object {
        private const val TAG = "AppMetricaAnalytics"
    }

    override fun trackEvent(name: String, params: Map<String, Any>) {
        try {
            if (params.isEmpty()) {
                YandexMetrica.reportEvent(name)
            } else {
                val json = JSONObject()
                params.forEach { (key, value) ->
                    json.put(key, value.toString())
                }
                YandexMetrica.reportEvent(name, json.toString())
            }
            Log.d(TAG, "📊 Event tracked: $name, params: $params")
        } catch (e: Exception) {
            Log.e(TAG, "Error tracking event: ${e.message}")
        }
    }

    override fun trackScreen(screenName: String) {
        try {
            val params = mapOf("screen_name" to screenName)
            trackEvent("screen_view", params)
        } catch (e: Exception) {
            Log.e(TAG, "Error tracking screen: ${e.message}")
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
            trackEvent("user_attribute", mapOf(key to value))
        } catch (e: Exception) {
            Log.e(TAG, "Error setting user attribute: ${e.message}")
        }
    }
}