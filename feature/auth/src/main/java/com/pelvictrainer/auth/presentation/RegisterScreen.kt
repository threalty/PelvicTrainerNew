package com.pelvictrainer.auth.presentation

import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.auth.presentation.components.AuthTextField

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    var openDocument by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.isLoginSuccess) {
        if (state.isLoginSuccess) {
            viewModel.consumeSuccess()
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Создать аккаунт",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Text(
            text = "Сохраняйте прогресс в облаке",
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
                AuthTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Имя",
                )

                AuthTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = "Email",
                    keyboardType = KeyboardType.Email,
                )

                AuthTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = "Пароль (мин. 8 символов)",
                    isPassword = true,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // ===== Согласие 1: Политика конфиденциальности =====
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onConsentPrivacyChange(!state.consentPrivacy) },
                    verticalAlignment = Alignment.Top,
                ) {
                    Checkbox(
                        checked = state.consentPrivacy,
                        onCheckedChange = viewModel::onConsentPrivacyChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Согласен с Политикой конфиденциальности (обязательно)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Прочитать Политику конфиденциальности",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable { openDocument = "privacy" }
                                .padding(top = 4.dp),
                        )
                    }
                }

                // ===== Согласие 2: Данные о здоровье =====
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onConsentHealthChange(!state.consentHealth) },
                    verticalAlignment = Alignment.Top,
                ) {
                    Checkbox(
                        checked = state.consentHealth,
                        onCheckedChange = viewModel::onConsentHealthChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Согласен на обработку данных о здоровье (ст. 10 152-ФЗ)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Прочитать медицинский дисклеймер",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable { openDocument = "disclaimer" }
                                .padding(top = 4.dp),
                        )
                    }
                }

                state.error?.let {
                    Text(
                        text = "❌ $it",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Button(
                    onClick = viewModel::register,
                    enabled = !state.isLoading
                            && state.name.isNotBlank()
                            && state.email.isNotBlank()
                            && state.password.length >= 8
                            && state.consentPrivacy
                            && state.consentHealth,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Зарегистрироваться", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("Уже есть аккаунт? Войти")
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    // ===== Всплывающее окно с документом =====
    openDocument?.let { doc ->
        LegalDocumentDialog(
            title = if (doc == "privacy") {
                "Политика конфиденциальности"
            } else {
                "Медицинский дисклеймер"
            },
            assetPath = if (doc == "privacy") {
                "file:///android_asset/legal/privacy.html"
            } else {
                "file:///android_asset/legal/disclaimer.html"
            },
            onDismiss = { openDocument = null },
        )
    }
}

@Composable
private fun LegalDocumentDialog(
    title: String,
    assetPath: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = false
                        setBackgroundColor(0x00000000)
                        loadUrl(assetPath)
                    }
                },
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        },
    )
}