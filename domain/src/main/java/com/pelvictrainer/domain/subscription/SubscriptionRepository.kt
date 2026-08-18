package com.pelvictrainer.domain.subscription

import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    val subscriptionState: Flow<SubscriptionState>

    suspend fun refreshFromServer()

    suspend fun activatePremium(plan: String, expiresAt: Long?)

    suspend fun deactivatePremium()

    suspend fun canStartTraining(): Boolean

    suspend fun getAvailablePresetIds(): List<Long>
}