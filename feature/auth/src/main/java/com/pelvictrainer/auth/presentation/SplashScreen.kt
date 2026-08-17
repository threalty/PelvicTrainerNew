package com.pelvictrainer.auth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.pelvictrainer.auth.presentation.navigation.AuthRoutes
import com.pelvictrainer.auth.presentation.navigation.navigateToLogin
import com.pelvictrainer.domain.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface SplashState {
    data object Loading : SplashState
    data object Authenticated : SplashState
    data object Unauthenticated : SplashState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepository,
) : ViewModel() {

    val state: StateFlow<SplashState> = flow {
        emit(SplashState.Loading)
        val loggedIn = authRepository.isLoggedIn()
        emit(
            if (loggedIn) SplashState.Authenticated
            else SplashState.Unauthenticated
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SplashState.Loading,
    )
}

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            SplashState.Loading -> { /* ждём */ }
            SplashState.Authenticated -> {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            SplashState.Unauthenticated -> {
                navController.navigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}