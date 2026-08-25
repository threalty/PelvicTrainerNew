package com.pelvictrainer.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.auth.presentation.components.AuthTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Setup2FAScreen(
    onSetupComplete: (List<String>) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TwoFAViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Если setup завершён и есть backup-коды - переходим дальше
    LaunchedEffect(state.backupCodes) {
        if (state.backupCodes.isNotEmpty() && state.is2FAEnabled == true) {
            onSetupComplete(state.backupCodes)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройка 2FA") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetSetup()
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (state.setupSecret == null) {
                // Шаг 1: Генерация секрета
                Text(
                    text = "🔐 Двухфакторная аутентификация",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "2FA добавит дополнительный уровень защиты вашему аккаунту. После ввода пароля вам потребуется 6-значный код из приложения-аутентификатора.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Рекомендуемые приложения:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "• Google Authenticator\n• Яндекс.Ключ\n• Microsoft Authenticator\n• Authy",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = viewModel::startSetup,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Начать настройку", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                // Шаг 2: QR-код и подтверждение
                Text(
                    text = "Шаг 1: Добавьте аккаунт",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Откройте приложение-аутентификатор и добавьте новый аккаунт вручную:",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Название:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "PelvicTrainer",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Секретный ключ:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        // Сохраняем в локальную переменную для smart cast
                        val secret = state.setupSecret
                        if (secret != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        shape = MaterialTheme.shapes.small,
                                    )
                                    .padding(12.dp),
                            ) {
                                Text(
                                    text = secret,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    ),
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Шаг 2: Подтвердите код",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Введите 6-значный код из приложения:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(12.dp))

                AuthTextField(
                    value = state.verifyCode,
                    onValueChange = viewModel::onVerifyCodeChange,
                    label = "6-значный код",
                    keyboardType = KeyboardType.Number,
                )

                state.error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "❌ $it",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = viewModel::verifySetup,
                    enabled = !state.isLoading && state.verifyCode.length == 6,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Подтвердить и включить 2FA", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.resetSetup()
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Отмена")
                }
            }
        }
    }
}