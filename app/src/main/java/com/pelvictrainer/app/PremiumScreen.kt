package com.pelvictrainer.app

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pelvictrainer.data.subscription.PaymentRequiredException
import com.pelvictrainer.domain.subscription.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    val isPremium = subscriptionRepository.subscriptionState.map { it.isPremiumActive }

    // Состояние для показа диалога
    private val _paymentError = mutableStateOf<String?>(null)
    val paymentError: String? get() = _paymentError.value

    fun requestPremium(plan: String) {
        viewModelScope.launch {
            try {
                subscriptionRepository.activatePremium(plan, null)
                // Если succeeded — подписка обновится автоматически через refreshFromServer
                _paymentError.value = null
            } catch (e: PaymentRequiredException) {
                // Ожидаемая ошибка — нужно связаться с поддержкой
                _paymentError.value = e.message ?: "Оплата временно недоступна"
            } catch (e: Exception) {
                _paymentError.value = "Ошибка: ${e.message}"
            }
        }
    }

    fun dismissError() {
        _paymentError.value = null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onNavigateBack: () -> Unit,
    viewModel: PremiumViewModel = hiltViewModel(),
) {
    val isPremium by viewModel.isPremium.collectAsState(initial = false)
    val paymentError = viewModel.paymentError
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Premium") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isPremium) {
                // === Пользователь уже Premium ===
                Spacer(modifier = Modifier.height(48.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "У вас уже Premium!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Все функции разблокированы",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                // === Показываем преимущества Premium ===
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Откройте все возможности",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Преимущества
                PremiumFeature(icon = Icons.Default.Check, text = "Неограниченные тренировки")
                PremiumFeature(icon = Icons.Default.Check, text = "Все программы тренировок")
                PremiumFeature(icon = Icons.Default.Check, text = "Календарь и статистика")
                PremiumFeature(icon = Icons.Default.Check, text = "Система достижений")
                PremiumFeature(icon = Icons.Default.Check, text = "Расширенная аналитика")

                Spacer(modifier = Modifier.height(32.dp))

                // === Информационная карточка о контакте ===
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "💎 Как получить Premium?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Оплата в приложении временно недоступна. Для активации Premium напишите нам на email:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "support@pelvictrainer.ru",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Укажите ваш email и желаемый план. Мы активируем Premium в течение 24 часов.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:support@pelvictrainer.ru")
                                    putExtra(Intent.EXTRA_SUBJECT, "Активация Premium PelvicTrainer")
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Здравствуйте!\n\nХочу активировать Premium подписку.\n\nМой email: ...\nПлан: месячный / годовой / навсегда\n\nСпасибо!"
                                    )
                                }
                                try {
                                    context.startActivity(emailIntent)
                                } catch (e: Exception) {
                                    // Если нет email клиента — просто копируем
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            )
                        ) {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Написать в поддержку")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Доступные планы (для информации)
                Text(
                    text = "Доступные планы",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(12.dp))

                PremiumPlanCard(
                    title = "Месяц",
                    price = "299 ₽/мес",
                    description = "Отмена в любой момент",
                    onClick = { viewModel.requestPremium("monthly") },
                )
                Spacer(modifier = Modifier.height(12.dp))

                PremiumPlanCard(
                    title = "Год",
                    price = "2 490 ₽/год",
                    description = "Экономия 30%",
                    onClick = { viewModel.requestPremium("yearly") },
                    highlighted = true,
                )
                Spacer(modifier = Modifier.height(12.dp))

                PremiumPlanCard(
                    title = "Навсегда",
                    price = "4 990 ₽",
                    description = "Одна покупка — навсегда",
                    onClick = { viewModel.requestPremium("lifetime") },
                )
            }
        }
    }

    // === Диалог ошибки оплаты ===
    if (paymentError != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            title = { Text("Активация Premium") },
            text = {
                Column {
                    Text(paymentError)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "📧 support@pelvictrainer.ru",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissError()
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@pelvictrainer.ru")
                            putExtra(Intent.EXTRA_SUBJECT, "Активация Premium PelvicTrainer")
                        }
                        try {
                            context.startActivity(emailIntent)
                        } catch (_: Exception) {}
                    }
                ) {
                    Text("Написать")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

@Composable
private fun PremiumFeature(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun PremiumPlanCard(
    title: String,
    price: String,
    description: String,
    onClick: () -> Unit,
    highlighted: Boolean = false,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (highlighted) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (highlighted) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (highlighted) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                ),
            ) {
                Text("Выбрать")
            }
        }
    }
}