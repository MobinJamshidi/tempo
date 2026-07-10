package com.mobinjam.tempo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.mobinjam.tempo.core.designsystem.theme.TempoTheme
import com.mobinjam.tempo.feature.auth.presentation.ForgotPasswordScreen
import com.mobinjam.tempo.feature.auth.presentation.LoginScreen
import com.mobinjam.tempo.feature.auth.presentation.SignUpScreen
import com.mobinjam.tempo.feature.main.MainScreen
import com.mobinjam.tempo.feature.splash.SplashScreen

private enum class AppScreen { Splash, Login, SignUp, ForgotPassword, Main }

@Composable
@Preview
fun App() {
    TempoTheme {
        var screen by remember { mutableStateOf(AppScreen.Splash) }

        when (screen) {
            AppScreen.Splash -> SplashScreen(
                onNavigateToLogin = { screen = AppScreen.Login },
                onNavigateToMain = { screen = AppScreen.Main },
            )
            AppScreen.Login -> LoginScreen(
                onLoginSuccess = { screen = AppScreen.Main },
                onCreateAccountClick = { screen = AppScreen.SignUp },
                onForgotPasswordClick = { screen = AppScreen.ForgotPassword },
            )
            AppScreen.SignUp -> SignUpScreen(
                onSignUpSuccess = { screen = AppScreen.Login },
                onBackToLogin = { screen = AppScreen.Login },
            )
            AppScreen.ForgotPassword -> ForgotPasswordScreen(
                onBackToLogin = { screen = AppScreen.Login },
            )
            AppScreen.Main -> MainScreen(
                onLogout = { screen = AppScreen.Login },
            )
        }
    }
}