package com.pelvictrainer.app

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PelvicTrainerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    companion object {
        private const val TAG = "PelvicTrainerApp"

        // API ключ из metrica.yandex.ru (SDK key)
        private const val APPMETRICA_API_KEY = "729dfe03-b3a5-4dac-9b7c-6a2a52ba43fd"

        // ID приложения в AppMetrica
        const val APP_ID = 6339955
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initAppMetrica()
    }

    private fun initAppMetrica() {
        try {
            val config = YandexMetricaConfig.newConfigBuilder(APPMETRICA_API_KEY)
                .withStatisticsSending(true)      // Отправка статистики
                .withCrashReporting(true)         // Краш-репорты (включает и отчёты об ошибках)
                .withLocationTracking(false)      // Не отслеживаем локацию (приватность)
                .withSessionTimeout(90)           // Сессия живёт 90 секунд после ухода в фон
                .build()

            YandexMetrica.activate(applicationContext, config)

            Log.d(TAG, "✅ AppMetrica успешно инициализирована (App ID: $APP_ID)")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка инициализации AppMetrica: ${e.message}")
            // Приложение продолжает работать без аналитики
        }
    }
}