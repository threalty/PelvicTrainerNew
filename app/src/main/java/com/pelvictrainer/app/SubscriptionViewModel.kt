package com.pelvictrainer.app

import androidx.lifecycle.ViewModel
import com.pelvictrainer.domain.subscription.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    val isPremium: Flow<Boolean> = subscriptionRepository.subscriptionState.map { it.isPremiumActive }
}