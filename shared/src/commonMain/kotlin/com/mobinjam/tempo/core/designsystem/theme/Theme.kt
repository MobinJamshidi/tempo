package com.mobinjam.tempo.core.designsystem.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
// ↓↓↓ match this package to the Res import in your App.kt ↓↓↓
import tempo.shared.generated.resources.Res
import tempo.shared.generated.resources.share_tech

private val TempoDarkColors = darkColorScheme(
    primary = Color(0xFF1B2A4A),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFF0A0A0B),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF141416),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF1E1E22),
    onSurfaceVariant = Color(0xFF8A8A90),
    outline = Color(0xFF2A2A2E),
)

@Composable
fun TempoTheme(content: @Composable () -> Unit) {
    val shareTech = FontFamily(Font(Res.font.share_tech))

    val base = Typography()
    val typography = Typography(
        displayLarge = base.displayLarge.copy(fontFamily = shareTech),
        displayMedium = base.displayMedium.copy(fontFamily = shareTech),
        displaySmall = base.displaySmall.copy(fontFamily = shareTech),
        headlineLarge = base.headlineLarge.copy(fontFamily = shareTech),
        headlineMedium = base.headlineMedium.copy(fontFamily = shareTech),
        headlineSmall = base.headlineSmall.copy(fontFamily = shareTech),
        titleLarge = base.titleLarge.copy(fontFamily = shareTech),
        titleMedium = base.titleMedium.copy(fontFamily = shareTech),
        titleSmall = base.titleSmall.copy(fontFamily = shareTech),
        bodyLarge = base.bodyLarge.copy(fontFamily = shareTech),
        bodyMedium = base.bodyMedium.copy(fontFamily = shareTech),
        bodySmall = base.bodySmall.copy(fontFamily = shareTech),
        labelLarge = base.labelLarge.copy(fontFamily = shareTech),
        labelMedium = base.labelMedium.copy(fontFamily = shareTech),
        labelSmall = base.labelSmall.copy(fontFamily = shareTech),
    )

    MaterialTheme(
        colorScheme = TempoDarkColors,
        typography = typography,
    ) {
        ProvideTextStyle(LocalTextStyle.current.copy(fontFamily = shareTech)) {
            content()
        }
    }
}