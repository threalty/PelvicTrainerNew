package com.pelvictrainer.app

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.pelvictrainer.auth.presentation.LoginScreen
import com.pelvictrainer.auth.presentation.RegisterScreen
import com.pelvictrainer.domain.model.TrainingLevel
import com.pelvictrainer.domain.repository.UserPreferencesRepository
import com.pelvictrainer.feature.TrainingScreen
import com.pelvictrainer.network.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class Screen(val route: String) {
    object AgeConsent : Screen("age_consent")
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
    private val userPreferencesRepository: UserPreferencesRepository,
    private val tokenStorage: TokenStorage // ДОБАВЛЕНО
) : ViewModel() {
    private val _isOnboardingCompleted = MutableStateFlow(false)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isAgeConsentGiven = MutableStateFlow(false)
    val isAgeConsentGiven: StateFlow<Boolean> = _isAgeConsentGiven.asStateFlow()

    // ДОБАВЛЕНО: глобальная проверка наличия токена авторизации
    val isLoggedIn: StateFlow<Boolean> = MutableStateFlow(tokenStorage.isLoggedIn).asStateFlow()

    init {
        viewModelScope.launch {
            _isOnboardingCompleted.value = userPreferencesRepository.isOnboardingCompleted().first()
            _isAgeConsentGiven.value = userPreferencesRepository.isAgeConsentGiven().first()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.completeOnboarding()
        }
    }

    fun giveAgeConsent() {
        viewModelScope.launch {
            userPreferencesRepository.giveAgeConsent()
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
    val isAgeConsentGiven by viewModel.isAgeConsentGiven.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState() // ДОБАВЛЕНО

    val startDestination = when {
        !isAgeConsentGiven -> Screen.AgeConsent.route
        !isOnboardingCompleted -> Screen.Onboarding.route
        !isLoggedIn -> "login"       // ИЗМЕНЕНО: принудительный вход вместо Splash
        else -> Screen.Main.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }
    ) {
        composable(
            route = Screen.AgeConsent.route,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            AgeConsentScreen(
                onConsentGiven = {
                    viewModel.giveAgeConsent()
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.AgeConsent.route) { inclusive = true }
                    }
                },
                onViewDocument = { document ->
                    navController.navigate("legal/$document")
                },
            )
        }

        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
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

        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.LevelSelection.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.LevelSelection.route) {
            LevelSelectionScreen(
                onLevelSelected = { level ->
                    viewModel.selectLevel(level)
                    // ИЗМЕНЕНО: после выбора уровня ведём на логин, а не на Main
                    navController.navigate("login") {
                        popUpTo(Screen.LevelSelection.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Main.route) {
            MainScreen(
                navController = navController,
                onStartTraining = { presetId ->
                    navController.navigate(Screen.Training.createRoute(presetId))
                },
                onLoggedOut = { // ДОБАВЛЕНО: обработка выхода
                    navController.navigate("login") {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Training.route,
            arguments = listOf(
                navArgument("presetId") { type = NavType.LongType }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(350))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(350)
                ) + fadeOut(animationSpec = tween(350))
            }
        ) { backStack ->
            val presetId = backStack.arguments?.getLong("presetId") ?: 1L
            TrainingScreen(
                presetId = presetId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "legal/{document}",
            arguments = listOf(navArgument("document") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentName = backStackEntry.arguments?.getString("document") ?: "privacy"
            com.pelvictrainer.app.legal.LegalDocumentScreen(
                document = when (documentName) {
                    "privacy" -> com.pelvictrainer.app.legal.LegalDocument.PRIVACY
                    "terms" -> com.pelvictrainer.app.legal.LegalDocument.TERMS
                    "disclaimer" -> com.pelvictrainer.app.legal.LegalDocument.DISCLAIMER
                    else -> com.pelvictrainer.app.legal.LegalDocument.PRIVACY
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // ДОБАВЛЕНО: Корневые маршруты авторизации
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}