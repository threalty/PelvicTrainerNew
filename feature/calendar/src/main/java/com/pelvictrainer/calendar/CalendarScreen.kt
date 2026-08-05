package com.pelvictrainer.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.calendar.components.DayDetailsCard
import com.pelvictrainer.calendar.components.MonthCalendar
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val trainingDates = uiState.sessions.map { session ->
        java.time.LocalDate.ofEpochDay(session.date / (24 * 60 * 60 * 1000))
    }.toSet()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Календарь тренировок") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Заголовок месяца с навигацией
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.changeMonth(uiState.currentMonth.minusMonths(1))
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Предыдущий месяц")
                }

                Text(
                    text = uiState.currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = {
                        viewModel.changeMonth(uiState.currentMonth.plusMonths(1))
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Следующий месяц")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Календарь
            MonthCalendar(
                yearMonth = uiState.currentMonth,
                trainingDates = trainingDates,
                selectedDate = uiState.selectedDate,
                onDateClick = { date -> viewModel.selectDate(date) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Детали выбранного дня — ИСПРАВЛЕНО: используем ?.let
            uiState.selectedDate?.let { selectedDate ->
                DayDetailsCard(
                    date = selectedDate,
                    sessions = uiState.sessionsForSelectedDate
                )
            } ?: Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Выберите день для просмотра деталей",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}