package com.pelvictrainer.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pelvictrainer.feature.TrainingScreen
import com.pelvictrainer.feature.TrainingSettingsScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "settings"
    ) {
        composable(route = "settings") {
            TrainingSettingsScreen(
                onStartTraining = { preset ->
                    navController.navigate("training/${preset.id}")
                }
            )
        }
        composable(
            route = "training/{presetId}",
            arguments = listOf(
                navArgument("presetId") { type = NavType.LongType }
            ),
        ) { backStack ->
            val presetId = backStack.arguments?.getLong("presetId") ?: 0L
            TrainingScreen(
                presetId = presetId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}