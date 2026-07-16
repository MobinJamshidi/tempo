package com.mobinjam.tempo.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)

private const val APP_VERSION = "1.0.0"
private const val SUPPORT_EMAIL = "jamshid.mobin567@gmail.com"
private const val GITHUB_HANDLE = "MobinJamshidi"
private const val TELEGRAM_HANDLE = "@mobinjam"
private const val INSTAGRAM_HANDLE = "@mobinjam"

@Composable
fun SupportPage(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(40.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "‹",
                color = AccentBlue,
                fontSize = 28.sp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onBack() }
                    .padding(horizontal = 8.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Support",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(28.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CardBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(AccentBlue, Color(0xFF2A7FFF))
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "T",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Tempo",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Version $APP_VERSION",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Built by Mobin Jamshidi",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Get in touch",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CardBg),
        ) {
            ContactRow(iconType = ProfileIconType.EMAIL, label = "Email", value = SUPPORT_EMAIL)
            ThinDivider()
            ContactRow(iconType = ProfileIconType.GITHUB, label = "GitHub", value = GITHUB_HANDLE)
            ThinDivider()
            ContactRow(iconType = ProfileIconType.TELEGRAM, label = "Telegram", value = TELEGRAM_HANDLE)
            ThinDivider()
            ContactRow(iconType = ProfileIconType.INSTAGRAM, label = "Instagram", value = INSTAGRAM_HANDLE)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Found a bug or have an idea? Reach out anytime — feedback makes Tempo better.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 18.sp,
        )

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun ContactRow(iconType: ProfileIconType, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(AccentBlue.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            ProfileIcon(type = iconType, color = AccentBlue, size = 18)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}