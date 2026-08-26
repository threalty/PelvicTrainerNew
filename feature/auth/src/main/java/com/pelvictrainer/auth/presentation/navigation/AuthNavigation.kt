package com.pelvictrainer.auth.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pelvictrainer.auth.presentation.BackupCodesScreen
import com.pelvictrainer.auth.presentation.ForgotPasswordScreen
import com.pelvictrainer.auth.presentation.LoginScreen
import com.pelvictrainer.auth.presentation.ProfileScreen
import com.pelvictrainer.auth.presentation.RegisterScreen
import com.pelvictrainer.auth.presentation.Setup2FAScreen
import com.pelvictrainer.auth.presentation.TwoFAVerificationScreen

object AuthRoutes {
    const val LOGIN = "auth/login"
    const val REGISTER = "auth/register"
    const val PROFILE = "auth/profile"
    const val FORGOT_PASSWORD = "auth/forgot-password"
    const val TWO_FA_VERIFY = "auth/2fa-verify/{userId}/{email}"
    const val SETUP_2FA = "auth/setup-2fa"
    const val BACKUP_CODES = "auth/backup-codes"

    fun twoFAVerify(userId: Int, email: String): String =
        "auth/2fa-verify/$userId/$email"
}

fun NavController.navigateToLogin() {
    navigate(AuthRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
}

fun NavController.navigateToProfile() {
    navigate(AuthRoutes.PROFILE)
}

fun NavController.navigateToTwoFAVerify(userId: Int, email: String) {
    navigate(AuthRoutes.twoFAVerify(userId, email))
}

fun NavGraphBuilder.authGraph(navController: NavController) {
    composable(AuthRoutes.LOGIN) {
        LoginScreen(
            onLoginSuccess = {
                navController.navigate("main") { popUpTo(AuthRoutes.LOGIN) { inclusive = true } }
            },
            onNavigateToRegister = { navController.navigate(AuthRoutes.REGISTER) },
            onNavigateToForgotPassword = { navController.navigate(AuthRoutes.FORGOT_PASSWORD) },
            onNavigateToTwoFA = { userId, email ->
                navController.navigate(AuthRoutes.twoFAVerify(userId, email))
            },
        )
    }
    composable(AuthRoutes.REGISTER) {
        RegisterScreen(
            onRegisterSuccess = {
                navController.navigate("main") { popUpTo(AuthRoutes.REGISTER) { inclusive = true } }
            },
            onNavigateToLogin = { navController.popBackStack() },
        )
    }
    composable(AuthRoutes.PROFILE) {
        ProfileScreen(
            onLoggedOut = { navController.navigate(AuthRoutes.LOGIN) { popUpTo(0) { inclusive = true } } },
            onNavigateToSetup2FA = { navController.navigate(AuthRoutes.SETUP_2FA) },
            onNavigateToBackupCodes = { navController.navigate(AuthRoutes.BACKUP_CODES) },
        )
    }
    composable(AuthRoutes.FORGOT_PASSWORD) {
        ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() })
    }
    composable(
        route = AuthRoutes.TWO_FA_VERIFY,
        arguments = listOf(
            navArgument("userId") { type = NavType.IntType },
            navArgument("email") { type = NavType.StringType },
        ),
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
        val email = backStackEntry.arguments?.getString("email") ?: ""
        TwoFAVerificationScreen(
            userId = userId,
            email = email,
            onVerifySuccess = {
                navController.navigate("main") { popUpTo(AuthRoutes.LOGIN) { inclusive = true } }
            },
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composable(AuthRoutes.SETUP_2FA) {
        Setup2FAScreen(
            onSetupComplete = {
                navController.navigate(AuthRoutes.BACKUP_CODES) {
                    popUpTo(AuthRoutes.SETUP_2FA) { inclusive = true }
                }
            },
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composable(AuthRoutes.BACKUP_CODES) {
        BackupCodesScreen(onNavigateBack = { navController.popBackStack() })
    }
}