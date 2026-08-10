package com.pelvictrainer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import com.pelvictrainer.feature.TrainingScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object LevelSelection : Screen("level_selection")
    object Main : Screen("main")
    object Training : Screen("training/{presetId}") {
        fun createRoute(presetId: Long) = "training/$presetId"
    }
}

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _isOnboardingCompleted = MutableStateFlow(false)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    init {
        viewModelScope.launch {
            _isOnboardingCompleted.value = userPreferencesRepository.isOnboardingCompleted().first()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.completeOnboarding()
        }
    }

    fun selectLevel(level: TrainingLevel) {
        viewModelScope.launch {
            userPreferencesRepository.updateTrainingLevel(level)
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    val destination = if (isOnboardingCompleted) {
                        Screen.Main.route
                    } else {
                        Screen.Onboarding.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.LevelSelection.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LevelSelection.route) {
            LevelSelectionScreen(
                onLevelSelected = { level ->
                    viewModel.selectLevel(level)
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.LevelSelection.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                navController = navController,
                onStartTraining = { presetId ->
                    navController.navigate(Screen.Training.createRoute(presetId))
                }
            )
        }

        composable(
            route = Screen.Training.route,
            arguments = listOf(
                navArgument("presetId") { type = NavType.LongType }
            )
        ) { backStack ->
            val presetId = backStack.arguments?.getLong("presetId") ?: 1L
            TrainingScreen(
                presetId = presetId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}