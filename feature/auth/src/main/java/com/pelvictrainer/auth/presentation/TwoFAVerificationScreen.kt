package com.pelvictrainer.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.auth.presentation.components.AuthTextField
import androidx.compose.ui.unit.sp

@Composable
fun TwoFAVerificationScreen(
    onVerifySuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoginSuccess) {
        if (state.isLoginSuccess) {
            viewModel.consumeSuccess()
            onVerifySuccess()
        }
    }

    // Если 2FA больше не требуется (пользователь вышел) - возвращаемся
    LaunchedEffect(state.requires2FAUserId) {
        if (state.requires2FAUserId == null) {
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "🔐",
            fontSize = 64.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Двухфакторная аутентификация",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Text(
            text = if (state.useBackupCode) {
                "Введите один из сохранённых backup-кодов"
            } else {
                "Введите 6-значный код из\nGoogle Authenticator или Яндекс.Ключ"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
        )

        Spacer(Modifier.height(32.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            ),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (state.useBackupCode) {
                    // Backup-код: 8 символов XXXX-XXXX
                    AuthTextField(
                        value = state.twoFACode,
                        onValueChange = viewModel::onTwoFACodeChange,
                        label = "Backup-код (например ABCD-EFGH)",
                    )
                } else {
                    // 6-значный код
                    AuthTextField(
                        value = state.twoFACode,
                        onValueChange = viewModel::onTwoFACodeChange,
                        label = "6-значный код",
                        keyboardType = KeyboardType.Number,
                    )
                }

                state.error?.let {
                    Text(
                        text = "❌ $it",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Button(
                    onClick = {
                        if (state.useBackupCode) {
                            viewModel.verifyBackupCode()
                        } else {
                            viewModel.verify2FACode()
                        }
                    },
                    enabled = !state.isLoading && state.twoFACode.isNotBlank(),
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
                        Text("Подтвердить", fontWeight = FontWeight.SemiBold)
                    }
                }

                TextButton(
                    onClick = viewModel::toggleBackupCodeMode,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        if (state.useBackupCode) "Использовать код из приложения"
                        else "Использовать backup-код",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = {
                viewModel.reset2FAState()
                onNavigateBack()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Отмена", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
