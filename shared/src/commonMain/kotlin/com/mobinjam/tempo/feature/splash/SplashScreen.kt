package com.mobinjam.tempo.feature.splash

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import tempo.shared.generated.resources.Res
import tempo.shared.generated.resources.antonio_thin
import tempo.shared.generated.resources.splash_image

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: SplashViewModel = koinViewModel(),
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    var started by remember { mutableStateOf(false) }
    val antonio = FontFamily(Font(Res.font.antonio_thin))

    val blurRadius by animateDpAsState(
        targetValue = if (started) 20.dp else 0.dp,
        animationSpec = tween(durationMillis = 700),
        label = "blur",
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "textAlpha",
    )

    LaunchedEffect(Unit) {
        delay(2000)      // image stays sharp
        started = true   // blur the image + reveal the text
    }

    LaunchedEffect(destination) {
        if (destination != SplashDestination.LOADING) {
            delay(3000)  // hold on the animation before navigating
            when (destination) {
                SplashDestination.MAIN -> onNavigateToMain()
                SplashDestination.LOGIN -> onNavigateToLogin()
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Image(
            painter = painterResource(Res.drawable.splash_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius),
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x66000000), Color(0xE6000000)),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .alpha(textAlpha),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = "Tempo",
                color = Color.White,
                fontSize = 60.sp,
                fontFamily = antonio,
                textAlign = TextAlign.End,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Find your rhythm.\nEvery minute you focus becomes who you are.",
                color = Color(0xFFB8C4E0),
                fontSize = 26.sp,
                textAlign = TextAlign.End,
            )
        }
    }
}