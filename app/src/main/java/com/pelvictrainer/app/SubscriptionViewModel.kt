package com.pelvictrainer.app

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.domain.subscription.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SubscriptionVM"
    }

    val isPremium: StateFlow<Boolean> = subscriptionRepository.subscriptionState
        .map { it.isPremiumActive }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        // Автоматически проверяем подписку при создании ViewModel
        refreshFromServer()
    }

    fun refreshFromServer() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "🔄 Проверяем подписку с сервера...")
                subscriptionRepository.refreshFromServer()
                val currentState = subscriptionRepository.subscriptionState
                    .stateIn(viewModelScope, SharingStarted.Eagerly, null).value
                Log.d(TAG, "✅ Подписка обновлена: isPremium=${currentState?.isPremiumActive}")
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Ошибка проверки подписки: ${e.message}")
            }
        }
    }
}