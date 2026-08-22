package com.pelvictrainer.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun AgeConsentScreen(
    onConsentGiven: () -> Unit,
    onViewDocument: (String) -> Unit,
) {
    var ageConfirmed by remember { mutableStateOf(false) }
    var privacyConfirmed by remember { mutableStateOf(false) }
    var termsConfirmed by remember { mutableStateOf(false) }
    var disclaimerConfirmed by remember { mutableStateOf(false) }

    val allConfirmed = ageConfirmed && privacyConfirmed && termsConfirmed && disclaimerConfirmed

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Подтверждение возраста",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Text(
            text = "Приложение предназначено для лиц старше 18 лет",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { ageConfirmed = !ageConfirmed }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(
                checked = ageConfirmed,
                onCheckedChange = { ageConfirmed = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Мне исполнилось 18 лет *",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ConsentCheckboxWithLink(
            checked = privacyConfirmed,
            onCheckedChange = { privacyConfirmed = it },
            textStart = "Я ознакомился с ",
            linkText = "Политикой конфиденциальности",
            textEnd = " и даю согласие на обработку персональных данных (152-ФЗ)",
            onLinkClick = { onViewDocument("privacy") },
        )

        Spacer(modifier = Modifier.height(12.dp))

        ConsentCheckboxWithLink(
            checked = termsConfirmed,
            onCheckedChange = { termsConfirmed = it },
            textStart = "Я принимаю условия ",
            linkText = "Пользовательского соглашения",
            textEnd = " (публичной оферты)",
            onLinkClick = { onViewDocument("terms") },
        )

        Spacer(modifier = Modifier.height(12.dp))

        ConsentCheckboxWithLink(
            checked = disclaimerConfirmed,
            onCheckedChange = { disclaimerConfirmed = it },
            textStart = "Я ознакомился с ",
            linkText = "Медицинским дисклеймером",
            textEnd = " и понимаю, что приложение не заменяет консультацию врача",
            onLinkClick = { onViewDocument("disclaimer") },
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "⚠️ Важно",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Данные о тренировках относятся к специальной категории (здоровье) согласно ст. 10 152-ФЗ. Продолжая использование, вы даёте явное согласие на их обработку.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onConsentGiven,
            enabled = allConfirmed,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = if (allConfirmed) "Подтвердить и продолжить" else "Отметьте все пункты",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ИП Кожокарь А.В., ИНН 366228711168",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ConsentCheckboxWithLink(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textStart: String,
    linkText: String,
    textEnd: String,
    onLinkClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f).padding(top = 12.dp)) {
            Row(modifier = Modifier.clickable { onCheckedChange(!checked) }) {
                Text(
                    text = textStart,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = linkText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = onLinkClick),
                )
                Text(
                    text = textEnd,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}