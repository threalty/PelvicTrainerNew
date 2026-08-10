package com.pelvictrainer.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pelvictrainer.calendar.components.DayDetailsCard
import com.pelvictrainer.calendar.components.MonthCalendar
import com.pelvictrainer.designsystem.components.EmptyState
import com.pelvictrainer.designsystem.components.PullToRefreshBox
import com.pelvictrainer.designsystem.util.rememberHapticHelper
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onNavigateToWorkouts: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val hapticHelper = rememberHapticHelper()
    val coroutineScope = rememberCoroutineScope()

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
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = {
                coroutineScope.launch {
                    viewModel.refresh()
                }
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            if (uiState.sessions.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.CalendarMonth,
                        title = "Пока нет тренировок",
                        description = "Завершите первую тренировку, чтобы увидеть её в календаре и отслеживать прогресс по дням",
                        primaryActionText = "Начать тренировку",
                        onPrimaryActionClick = {
                            hapticHelper.mediumTap()
                            onNavigateToWorkouts()
                        }
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                hapticHelper.lightTap()
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
                                hapticHelper.lightTap()
                                viewModel.changeMonth(uiState.currentMonth.plusMonths(1))
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Следующий месяц")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    MonthCalendar(
                        yearMonth = uiState.currentMonth,
                        trainingDates = trainingDates,
                        selectedDate = uiState.selectedDate,
                        onDateClick = { date ->
                            hapticHelper.lightTap()
                            viewModel.selectDate(date)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
    }
}