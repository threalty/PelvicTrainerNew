package com.pelvictrainer.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pelvictrainer.achievements.AchievementsScreen
import com.pelvictrainer.calendar.CalendarScreen
import com.pelvictrainer.designsystem.theme.PelvicTrainerTheme
import com.pelvictrainer.domain.model.ThemeMode
import com.pelvictrainer.settings.SettingsScreen
import com.pelvictrainer.statistics.StatisticsScreen
import com.pelvictrainer.workouts.WorkoutsScreen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Главная")
    object Workouts : BottomNavItem("workouts", Icons.Default.FitnessCenter, "Тренировки")
    object Calendar : BottomNavItem("calendar", Icons.Default.CalendarMonth, "Календарь")
    object Statistics : BottomNavItem("statistics", Icons.Default.BarChart, "Статистика")
    object Achievements : BottomNavItem("achievements", Icons.Default.EmojiEvents, "Достижения")
    object Settings : BottomNavItem("settings_nav", Icons.Default.Settings, "Настройки")
}

@Composable
fun MainScreen(
    navController: NavHostController,
    onStartTraining: (Long) -> Unit
) {
    val mainNavController = rememberNavController()
    val prefsViewModel: MainScreenViewModel = hiltViewModel()
    val prefs by prefsViewModel.repository.userPreferences.collectAsState(
        initial = com.pelvictrainer.domain.model.UserPreferences()
    )

    val isDarkTheme = when (prefs.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val accentColor = Color(prefs.accentColor.argb)

    PelvicTrainerTheme(
        darkTheme = isDarkTheme,
        primary = accentColor
    ) {
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
                    HomeScreen(
                        onOpenWorkoutsList = {
                            mainNavController.navigate(BottomNavItem.Workouts.route)
                        }
                    )
                }
                composable(BottomNavItem.Workouts.route) {
                    WorkoutsScreen(
                        onNavigateBack = {
                            mainNavController.popBackStack()
                        },
                        onWorkoutSelected = { presetId ->
                            onStartTraining(presetId)
                        }
                    )
                }
                composable(BottomNavItem.Calendar.route) {
                    CalendarScreen()
                }
                composable(BottomNavItem.Statistics.route) {
                    StatisticsScreen()
                }
                composable(BottomNavItem.Achievements.route) {
                    AchievementsScreen()
                }
                composable(BottomNavItem.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Workouts,
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