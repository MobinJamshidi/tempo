package com.mobinjam.tempo.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobinjam.tempo.feature.badges.domain.Badge
import com.mobinjam.tempo.feature.badges.domain.allBadges
import com.mobinjam.tempo.feature.badges.presentation.BadgeIcon

private val AccentBlue = Color(0xFF3AC6FF)

@Composable
fun AchievementsPage(
    unlockedIds: Set<String>,
    onBack: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        contentPadding = PaddingValues(top = 40.dp, bottom = 110.dp),
    ) {
        item(span = { GridItemSpan(3) }) {
            Column {
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Achievements",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "${unlockedIds.size} of ${allBadges.size} unlocked",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        items(allBadges) { badge ->
            BadgeCell(
                badge = badge,
                unlocked = unlockedIds.contains(badge.id),
            )
        }
    }
}

@Composable
private fun BadgeCell(badge: Badge, unlocked: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            BadgeIcon(
                category = badge.category,
                unlocked = unlocked,
                size = 64,
            )
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (unlocked) AccentBlue else Color(0xFF2A3040)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (unlocked) "✓" else "🔒",
                    color = Color.White,
                    fontSize = if (unlocked) 12.sp else 9.sp,
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = badge.title,
            color = if (unlocked) MaterialTheme.colorScheme.onBackground
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Text(
            text = badge.description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 10.sp,
        )
    }
}