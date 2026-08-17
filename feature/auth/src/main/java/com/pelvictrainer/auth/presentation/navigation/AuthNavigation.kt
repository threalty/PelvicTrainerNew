package com.pelvictrainer.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pelvictrainer.auth.presentation.LoginScreen
import com.pelvictrainer.auth.presentation.ProfileScreen
import com.pelvictrainer.auth.presentation.RegisterScreen

object AuthRoutes {
    const val LOGIN = "auth/login"
    const val REGISTER = "auth/register"
    const val PROFILE = "auth/profile"
}

fun NavController.navigateToLogin() {
    navigate(AuthRoutes.LOGIN) {
        popUpTo(0) { inclusive = true }
    }
}

fun NavController.navigateToProfile() {
    navigate(AuthRoutes.PROFILE)
}

fun NavGraphBuilder.authGraph(
    navController: NavController,
) {
    composable(AuthRoutes.LOGIN) {
        LoginScreen(
            onLoginSuccess = {
                navController.navigate("main") {
                    popUpTo(AuthRoutes.LOGIN) { inclusive = true }
                }
            },
            onNavigateToRegister = {
                navController.navigate(AuthRoutes.REGISTER)
            },
        )
    }

    composable(AuthRoutes.REGISTER) {
        RegisterScreen(
            onRegisterSuccess = {
                navController.navigate("main") {
                    popUpTo(AuthRoutes.REGISTER) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            },
        )
    }

    composable(AuthRoutes.PROFILE) {
        ProfileScreen(
            onLoggedOut = {
                navController.navigate(AuthRoutes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            },
        )
    }
}