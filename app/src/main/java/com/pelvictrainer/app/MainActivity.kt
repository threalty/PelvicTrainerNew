package com.pelvictrainer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pelvictrainer.auth.presentation.SplashScreen
import com.pelvictrainer.auth.presentation.navigation.AuthRoutes
import com.pelvictrainer.auth.presentation.navigation.authGraph
import com.pelvictrainer.designsystem.theme.PelvicTrainerTheme
import com.pelvictrainer.feature.TrainingScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PelvicTrainerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val rootNavController = rememberNavController()

                    NavHost(
                        navController = rootNavController,
                        startDestination = "splash",
                    ) {
                        // 1. Сплэш — проверка авторизации (уже перенаправляет на логин, если не авторизован)
                        composable("splash") {
                            SplashScreen(navController = rootNavController)
                        }

                        // 2. Граф авторизации (login / register / profile)
                        authGraph(navController = rootNavController)

                        // 3. Главный экран с BottomNav
                        composable("main") {
                            MainScreen(
                                navController = rootNavController,
                                onStartTraining = { presetId ->
                                    rootNavController.navigate("training/$presetId")
                                },
                                // ДОБАВЛЕНО: корректный выход из аккаунта с очисткой стека
                                onLoggedOut = {
                                    rootNavController.navigate(AuthRoutes.LOGIN) {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 4. Экран тренировки
                        composable(
                            route = "training/{presetId}",
                            arguments = listOf(
                                navArgument("presetId") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val presetId = backStackEntry.arguments?.getLong("presetId") ?: 1L
                            TrainingScreen(
                                presetId = presetId,
                                onNavigateBack = {
                                    rootNavController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}