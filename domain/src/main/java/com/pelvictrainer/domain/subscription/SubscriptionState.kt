package com.pelvictrainer.domain.subscription

data class SubscriptionState(
    val isPremium: Boolean = false,
    val plan: String = "free",
    val expiresAt: Long? = null,
) {
    val isExpired: Boolean
        get() = expiresAt != null && expiresAt < System.currentTimeMillis()

    val isPremiumActive: Boolean
        get() = isPremium && !isExpired
}