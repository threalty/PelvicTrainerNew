package com.pelvictrainer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pelvictrainer.auth.presentation.SplashScreen
import com.pelvictrainer.auth.presentation.navigation.authGraph
import com.pelvictrainer.designsystem.theme.PelvicTrainerTheme
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
                        // 1. Сплэш — проверка авторизации
                        composable("splash") {
                            SplashScreen(navController = rootNavController)
                        }

                        // 2. Граф авторизации (login / register / profile)
                        authGraph(navController = rootNavController)

                        // 3. Главный экран с BottomNav (все ваши существующие табы)
                        composable("main") {
                            MainScreen(
                                navController = rootNavController,
                                onStartTraining = { presetId ->
                                    // TODO Sprint 8: навигация на экран тренировки
                                    // Сейчас просто игнорируем — ваше приложение продолжит работать как раньше
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}