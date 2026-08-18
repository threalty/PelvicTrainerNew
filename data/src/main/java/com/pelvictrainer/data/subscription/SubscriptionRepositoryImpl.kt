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
            return
        }

        try {
            val response = api.getMySubscription()
            Log.d(TAG, "Подписка с сервера: has=${response.hasSubscription}, plan=${response.plan}")

            context.subscriptionDataStore.edit { prefs ->
                prefs[KEY_IS_PREMIUM] = response.hasSubscription
                prefs[KEY_PLAN] = response.plan
                prefs[KEY_EXPIRES_AT] = response.expiresAt?.let { parseDate(it) } ?: 0L
            }
        } catch (e: Exception) {
            Log.w(TAG, "Не удалось обновить подписку с сервера: ${e.message}")
        }
    }

    override suspend fun activatePremium(plan: String, expiresAt: Long?) {
        context.subscriptionDataStore.edit { prefs ->
            prefs[KEY_IS_PREMIUM] = true
            prefs[KEY_PLAN] = plan
            prefs[KEY_EXPIRES_AT] = expiresAt ?: 0L
        }
        Log.d(TAG, "✅ Premium активирован: plan=$plan")
    }

    override suspend fun deactivatePremium() {
        context.subscriptionDataStore.edit { prefs ->
            prefs[KEY_IS_PREMIUM] = false
            prefs[KEY_PLAN] = "free"
            prefs[KEY_EXPIRES_AT] = 0L
        }
        Log.d(TAG, "Premium деактивирован")
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

        return if (state.isPremiumActive) {
            listOf(1L, 2L, 3L)
        } else {
            listOf(1L)
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