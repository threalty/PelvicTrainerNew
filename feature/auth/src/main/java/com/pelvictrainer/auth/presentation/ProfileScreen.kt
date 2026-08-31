package com.pelvictrainer.auth.presentation

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    twoFAViewModel: TwoFAViewModel = hiltViewModel(),
    onLoggedOut: () -> Unit,
    onNavigateToSetup2FA: () -> Unit,
    onNavigateToBackupCodes: () -> Unit,
) {
    val authState by authViewModel.state.collectAsState()
    val twoFAState by twoFAViewModel.state.collectAsState()

    var showDisableDialog by remember { mutableStateOf(false) }
    var showDisableSuccess by remember { mutableStateOf(false) }
    var showRegenerateDialog by remember { mutableStateOf(false) }
    var showRegenerateSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        twoFAViewModel.load2FAStatus()
    }

    // Отслеживаем успешное отключение 2FA
    LaunchedEffect(twoFAState.is2FAEnabled) {
        if (twoFAState.is2FAEnabled == false && showDisableDialog) {
            showDisableDialog = false
            showDisableSuccess = true
        }
    }

    // Отслеживаем успешную регенерацию — переходим на экран с новыми кодами
    LaunchedEffect(twoFAState.backupCodes, showRegenerateDialog) {
        if (twoFAState.backupCodes.isNotEmpty() && showRegenerateDialog) {
            showRegenerateDialog = false
            showRegenerateSuccess = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "Аккаунт",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            ),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = authState.email.ifBlank { "Неизвестный пользователь" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        authState.name.takeIf { it.isNotBlank() }?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = authState.email.ifBlank { "—" },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // === Секция 2FA ===
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            ),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Двухфакторная аутентификация",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (twoFAState.isLoading && twoFAState.backupCodes.isEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Загрузка...",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    val is2FAEnabled = twoFAState.is2FAEnabled
                    Text(
                        text = if (is2FAEnabled == true) {
                            "✅ 2FA включена"
                        } else if (is2FAEnabled == false) {
                            "❌ 2FA выключена"
                        } else {
                            "Статус неизвестен"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (is2FAEnabled == true) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )

                    Spacer(Modifier.height(12.dp))

                    if (is2FAEnabled == false) {
                        Text(
                            text = "Добавьте дополнительный уровень защиты вашему аккаунту.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onNavigateToSetup2FA,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Включить 2FA")
                        }
                    } else if (is2FAEnabled == true) {
                        Text(
                            text = "Ваш аккаунт защищён двухфакторной аутентификацией.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(12.dp))

                        // === Первая строка кнопок ===
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            TextButton(
                                onClick = onNavigateToBackupCodes,
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("Backup-коды")
                            }
                            TextButton(
                                onClick = { showRegenerateDialog = true },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("🔄 Новые коды")
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        // === Вторая строка кнопок ===
                        TextButton(
                            onClick = { showDisableDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            Text("Отключить 2FA")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
            ),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "💎 Синхронизация",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Ваши тренировки автоматически сохраняются в облако и доступны на всех устройствах.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                onLoggedOut()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
            ),
        ) {
            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Выйти из аккаунта")
        }
    }

    // === Диалог отключения 2FA ===
    if (showDisableDialog) {
        Disable2FADialog(
            isLoading = twoFAState.isLoading,
            error = twoFAState.error,
            onConfirm = { code ->
                twoFAViewModel.disable2FA(code)
            },
            onDismiss = {
                showDisableDialog = false
                twoFAViewModel.clearError()
            },
        )
    }

    // === Диалог регенерации backup-кодов ===
    if (showRegenerateDialog) {
        RegenerateCodesDialog(
            isLoading = twoFAState.isLoading,
            error = twoFAState.error,
            onConfirm = { code ->
                twoFAViewModel.regenerateBackupCodes(code)
            },
            onDismiss = {
                showRegenerateDialog = false
                twoFAViewModel.clearError()
            },
        )
    }

    // === Диалог успешного отключения ===
    if (showDisableSuccess) {
        AlertDialog(
            onDismissRequest = { showDisableSuccess = false },
            title = {
                Text(
                    text = "✅ 2FA отключена",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Двухфакторная аутентификация была успешно отключена. Теперь для входа достаточно пароля.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDisableSuccess = false
                    twoFAViewModel.load2FAStatus()
                }) {
                    Text("OK")
                }
            },
        )
    }

    // === Диалог успешной регенерации — предлагаем посмотреть новые коды ===
    if (showRegenerateSuccess) {
        AlertDialog(
            onDismissRequest = { showRegenerateSuccess = false },
            title = {
                Text(
                    text = "🔄 Новые коды получены!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Backup-коды были успешно обновлены. Старые коды больше недействительны. Обязательно сохраните новые коды в надёжном месте.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showRegenerateSuccess = false
                    onNavigateToBackupCodes()
                }) {
                    Text("Показать коды")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegenerateSuccess = false }) {
                    Text("Позже")
                }
            },
        )
    }
}