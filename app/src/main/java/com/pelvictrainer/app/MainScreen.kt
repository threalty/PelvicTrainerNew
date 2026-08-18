package com.pelvictrainer.app

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pelvictrainer.achievements.AchievementsScreen
import com.pelvictrainer.app.legal.LegalDocument
import com.pelvictrainer.app.legal.LegalDocumentScreen
import com.pelvictrainer.auth.presentation.LoginScreen
import com.pelvictrainer.auth.presentation.ProfileScreen
import com.pelvictrainer.auth.presentation.RegisterScreen
import com.pelvictrainer.calendar.CalendarScreen
import com.pelvictrainer.designsystem.theme.PelvicTrainerTheme
import com.pelvictrainer.domain.model.ThemeMode
import com.pelvictrainer.settings.SettingsScreen
import com.pelvictrainer.statistics.StatisticsScreen
import com.pelvictrainer.workouts.WorkoutsScreen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String,
) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Главная")
    object Workouts : BottomNavItem("workouts", Icons.Default.FitnessCenter, "Тренировки")
    object Calendar : BottomNavItem("calendar", Icons.Default.CalendarMonth, "Календарь")
    object Statistics : BottomNavItem("statistics", Icons.Default.BarChart, "Статистика")
    object Achievements : BottomNavItem("achievements", Icons.Default.EmojiEvents, "Достижения")
    object Settings : BottomNavItem("settings_nav", Icons.Default.Settings, "Настройки")
}

private object ProfileRoute {
    const val ROUTE = "profile"
}

@Composable
fun MainScreen(
    navController: NavHostController,
    onStartTraining: (Long) -> Unit,
) {
    val mainNavController = rememberNavController()
    val prefsViewModel: MainScreenViewModel = hiltViewModel()
    val prefs by prefsViewModel.repository.userPreferences.collectAsState(
        initial = com.pelvictrainer.domain.model.UserPreferences(),
    )

    val isDarkTheme = when (prefs.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val accentColor = Color(prefs.accentColor.argb)

    PelvicTrainerTheme(
        darkTheme = isDarkTheme,
        primary = accentColor,
    ) {
        Scaffold(
            bottomBar = {
                ExpandedBottomBar(mainNavController = mainNavController)
            },
        ) { innerPadding ->
            NavHost(
                navController = mainNavController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        onOpenWorkoutsList = {
                            mainNavController.navigate(BottomNavItem.Workouts.route) {
                                popUpTo(mainNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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
                        },
                        onNavigateToPremium = {
                            mainNavController.navigate("premium")
                        },
                    )
                }
                composable(BottomNavItem.Calendar.route) {
                    CalendarScreen(
                        onNavigateToWorkouts = {
                            mainNavController.navigate(BottomNavItem.Workouts.route) {
                                popUpTo(mainNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(BottomNavItem.Statistics.route) {
                    StatisticsScreen(
                        onNavigateToWorkouts = {
                            mainNavController.navigate(BottomNavItem.Workouts.route) {
                                popUpTo(mainNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToPremium = {
                            mainNavController.navigate("premium")
                        },
                    )
                }
                composable(BottomNavItem.Achievements.route) {
                    AchievementsScreen(
                        onNavigateToWorkouts = {
                            mainNavController.navigate(BottomNavItem.Workouts.route) {
                                popUpTo(mainNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToPremium = {
                            mainNavController.navigate("premium")
                        },
                    )
                }
                composable(BottomNavItem.Settings.route) {
                    SettingsScreen(
                        navController = mainNavController,
                        onNavigateToPremium = {
                            mainNavController.navigate("premium")
                        },
                        onNavigateToLegal = { document ->
                            mainNavController.navigate("legal/$document")
                        },
                    )
                }

                // ===== Профиль =====
                composable(ProfileRoute.ROUTE) {
                    ProfileScreen(
                        onLoggedOut = {
                            mainNavController.popBackStack()
                        },
                    )
                }

                // ===== Вход =====
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            mainNavController.popBackStack()
                        },
                        onNavigateToRegister = {
                            mainNavController.navigate("register")
                        },
                    )
                }

                // ===== Регистрация =====
                composable("register") {
                    RegisterScreen(
                        onRegisterSuccess = {
                            mainNavController.popBackStack()
                        },
                        onNavigateToLogin = {
                            mainNavController.popBackStack()
                        },
                    )
                }

                // ===== Premium экран =====
                composable("premium") {
                    PremiumScreen(
                        onNavigateBack = {
                            mainNavController.popBackStack()
                        },
                    )
                }

                // ===== Юридические документы (НОВОЕ) =====
                composable(
                    route = "legal/{document}",
                    arguments = listOf(navArgument("document") { type = NavType.StringType })
                ) { backStackEntry ->
                    val documentName = backStackEntry.arguments?.getString("document") ?: "privacy"
                    val document = when (documentName) {
                        "privacy" -> LegalDocument.PRIVACY
                        "terms" -> LegalDocument.TERMS
                        "disclaimer" -> LegalDocument.DISCLAIMER
                        else -> LegalDocument.PRIVACY
                    }
                    LegalDocumentScreen(
                        document = document,
                        onNavigateBack = {
                            mainNavController.popBackStack()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandedBottomBar(mainNavController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Workouts,
        BottomNavItem.Calendar,
        BottomNavItem.Statistics,
        BottomNavItem.Achievements,
        BottomNavItem.Settings,
    )

    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                ExpandedNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = {
                        mainNavController.navigate(item.route) {
                            popUpTo(mainNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.weight(if (isSelected) 3.5f else 1f),
                )
            }
        }
    }
}

@Composable
private fun ExpandedNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            )
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(22.dp),
        )

        if (isSelected) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = item.label,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}