package com.pelvictrainer.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pelvictrainer.calendar.CalendarScreen
import com.pelvictrainer.feature.TrainingSettingsScreen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Главная")
    object Calendar : BottomNavItem("calendar", Icons.Default.CalendarMonth, "Календарь")
    object Statistics : BottomNavItem("statistics", Icons.Default.BarChart, "Статистика")
    object Achievements : BottomNavItem("achievements", Icons.Default.EmojiEvents, "Достижения")
    object Settings : BottomNavItem("settings_nav", Icons.Default.Settings, "Настройки")
}

@Composable
fun MainScreen(
    navController: NavHostController,
    onStartTraining: () -> Unit
) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(mainNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(onStartTraining = onStartTraining)
            }
            composable(BottomNavItem.Calendar.route) {
                CalendarScreen()
            }
            composable(BottomNavItem.Statistics.route) {
                PlaceholderScreen(title = "Статистика")
            }
            composable(BottomNavItem.Achievements.route) {
                PlaceholderScreen(title = "Достижения")
            }
            composable(BottomNavItem.Settings.route) {
                TrainingSettingsScreen(onStartTraining = { /* Не используется */ })
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Calendar,
        BottomNavItem.Statistics,
        BottomNavItem.Achievements,
        BottomNavItem.Settings
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title\n(в разработке)",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}