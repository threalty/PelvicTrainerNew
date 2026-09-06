package com.pelvictrainer.data.subscription

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pelvictrainer.domain.repository.TrainingRepository
import com.pelvictrainer.domain.subscription.SubscriptionRepository
import com.pelvictrainer.domain.subscription.SubscriptionState
import com.pelvictrainer.network.CreatePaymentRequest
import com.pelvictrainer.network.PelvicApi
import com.pelvictrainer.network.TokenStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

private val Context.subscriptionDataStore by preferencesDataStore(name = "subscription")

/**
 * Исключение для случаев, когда требуется внешняя оплата (свяжитесь с поддержкой).
 * Мобильное приложение не может активировать Premium самостоятельно.
 */
class PaymentRequiredException(message: String) : Exception(message)

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: PelvicApi,
    private val tokenStorage: TokenStorage,
    private val trainingRepository: TrainingRepository,
) : SubscriptionRepository {

    companion object {
        private const val TAG = "SubscriptionRepo"
        private val KEY_IS_PREMIUM = booleanPreferencesKey("is_premium")
        private val KEY_PLAN = stringPreferencesKey("plan")
        private val KEY_EXPIRES_AT = longPreferencesKey("expires_at")
    }

    override val subscriptionState: Flow<SubscriptionState> =
        context.subscriptionDataStore.data.map { prefs ->
            SubscriptionState(
                isPremium = prefs[KEY_IS_PREMIUM] ?: false,
                plan = prefs[KEY_PLAN] ?: "free",
                expiresAt = prefs[KEY_EXPIRES_AT]?.takeIf { it > 0 },
            )
        }

    override suspend fun refreshFromServer() {
        if (!tokenStorage.isLoggedIn) {
            Log.d(TAG, "Пропуск refresh: пользователь не залогинен")
            deactivatePremium()
            return
        }

        try {
            val response = api.getMySubscription()
            Log.d(TAG, "📥 Подписка с сервера: has=${response.hasSubscription}, plan=${response.plan}")

            // ВАЖНО: доверяем только серверу
            context.subscriptionDataStore.edit { prefs ->
                prefs[KEY_IS_PREMIUM] = response.hasSubscription
                prefs[KEY_PLAN] = response.plan ?: "free"
                prefs[KEY_EXPIRES_AT] = response.expiresAt?.let { parseDate(it) } ?: 0L
            }
        } catch (e: Exception) {
            // При ошибке сети НЕ сбрасываем локальную подписку — пусть остаётся как была
            Log.w(TAG, "⚠️ Не удалось обновить подписку с сервера: ${e.message}. Используем локальный кэш.")
        }
    }

    /**
     * Активация Premium — ТОЛЬКО через сервер (реальная оплата или админская активация).
     * Никакого fallback на локальную активацию.
     */
    override suspend fun activatePremium(plan: String, expiresAt: Long?) {
        if (!tokenStorage.isLoggedIn) {
            throw IllegalStateException("Требуется авторизация для активации Premium")
        }

        try {
            Log.d(TAG, "🌐 Создание платежа на сервере: plan=$plan")
            val response = api.createPayment(CreatePaymentRequest(plan))
            Log.d(TAG, "📥 Ответ сервера: payment_id=${response.paymentId}, status=${response.status}")

            // Если сервер вернул "pending" — значит оплата не подключена, нужна связь с поддержкой
            if (response.status == "pending") {
                throw PaymentRequiredException(
                    response.message.ifBlank {
                        "Оплата временно недоступна. Свяжитесь с поддержкой."
                    }
                )
            }

            // Если сервер ответил "succeeded" (например админ активировал) — синхронизируемся
            if (response.status == "succeeded") {
                refreshFromServer()
                return
            }

            // Неизвестный статус — тоже показываем ошибку
            throw PaymentRequiredException(
                response.message.ifBlank {
                    "Оплата временно недоступна. Свяжитесь с поддержкой."
                }
            )
        } catch (e: PaymentRequiredException) {
            // Пробрасываем дальше, чтобы UI показал сообщение
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка активации Premium: ${e.message}")
            throw PaymentRequiredException(
                "Не удалось создать платёж. Свяжитесь с поддержкой: support@pelvictrainer.ru"
            )
        }
    }

    override suspend fun deactivatePremium() {
        context.subscriptionDataStore.edit { prefs ->
            prefs[KEY_IS_PREMIUM] = false
            prefs[KEY_PLAN] = "free"
            prefs[KEY_EXPIRES_AT] = 0L
        }
        Log.d(TAG, "🆓 Premium деактивирован")
    }

    override suspend fun canStartTraining(): Boolean {
        val state = subscriptionState.first()

        if (state.isPremiumActive) return true

        val today = LocalDate.now()
        val todayStartMillis = today.atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        val sessions = trainingRepository.getSessions().first()
        val hasTrainingToday = sessions.any { it.date >= todayStartMillis }

        return !hasTrainingToday
    }

    override suspend fun getAvailablePresetIds(): List<Long> {
        val state = subscriptionState.first()
        if (state.isPremiumActive) {
            return trainingRepository.getPresets().first().map { it.id }
        } else {
            return trainingRepository.getPresets().first()
                .filter { it.level == com.pelvictrainer.domain.model.TrainingLevel.BEGINNER }
                .map { it.id }
        }
    }

    private fun parseDate(dateStr: String): Long {
        return try {
            ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME)
                .toInstant().toEpochMilli()
        } catch (e: Exception) {
            0L
        }
    }
}