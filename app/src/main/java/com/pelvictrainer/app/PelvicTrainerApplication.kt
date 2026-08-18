package com.pelvictrainer.app

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.pelvictrainer.data.sync.SyncScheduler
import com.pelvictrainer.domain.subscription.SubscriptionRepository
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PelvicTrainerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var syncScheduler: SyncScheduler

    @Inject
    lateinit var subscriptionRepository: SubscriptionRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "PelvicTrainerApp"
        private const val APPMETRICA_API_KEY = "729dfe03-b3a5-4dac-9b7c-6a2a52ba43fd"
        const val APP_ID = 6339955
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initAppMetrica()
        syncScheduler.schedule()
        refreshSubscription()
    }

    private fun initAppMetrica() {
        try {
            val config = YandexMetricaConfig.newConfigBuilder(APPMETRICA_API_KEY)
                .withStatisticsSending(true)
                .withCrashReporting(true)
                .withLocationTracking(false)
                .withSessionTimeout(90)
                .build()
            YandexMetrica.activate(applicationContext, config)
            Log.d(TAG, "✅ AppMetrica успешно инициализирована (App ID: $APP_ID)")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка инициализации AppMetrica: ${e.message}")
        }
    }

    private fun refreshSubscription() {
        applicationScope.launch {
            subscriptionRepository.refreshFromServer()
        }
    }
}