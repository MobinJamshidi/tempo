package com.mobinjam.tempo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.mobinjam.tempo.core.designsystem.theme.TempoTheme
import com.mobinjam.tempo.feature.auth.presentation.LoginScreen
import com.mobinjam.tempo.feature.splash.SplashScreen

private enum class AppScreen { Splash, Login }

@Composable
@Preview
fun App() {
    TempoTheme {
        var screen by remember { mutableStateOf(AppScreen.Splash) }

        when (screen) {
            AppScreen.Splash -> SplashScreen(onFinished = { screen = AppScreen.Login })
            AppScreen.Login -> LoginScreen()
        }
    }
}
