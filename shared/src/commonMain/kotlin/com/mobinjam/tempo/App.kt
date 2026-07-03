package com.mobinjam.tempo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.mobinjam.tempo.core.designsystem.theme.TempoTheme
import com.mobinjam.tempo.feature.auth.presentation.LoginScreen
import com.mobinjam.tempo.feature.auth.presentation.SignUpScreen
import com.mobinjam.tempo.feature.home.HomeScreen
import com.mobinjam.tempo.feature.splash.SplashScreen

private enum class AppScreen { Splash, Login, SignUp, Home }

@Composable
@Preview
fun App() {
    TempoTheme {
        var screen by remember { mutableStateOf(AppScreen.Splash) }

        when (screen) {
            AppScreen.Splash -> SplashScreen(
                onFinished = { screen = AppScreen.Login },
            )
            AppScreen.Login -> LoginScreen(
                onLoginSuccess = { screen = AppScreen.Home },
                onCreateAccountClick = { screen = AppScreen.SignUp },
            )
            AppScreen.SignUp -> SignUpScreen(
                onSignUpSuccess = { screen = AppScreen.Login },
                onBackToLogin = { screen = AppScreen.Login },
            )
            AppScreen.Home -> HomeScreen(
                onLogout = { screen = AppScreen.Login },
            )
        }
    }
}