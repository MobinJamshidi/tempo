package com.mobinjam.tempo.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.feature.badges.domain.Badge
import com.mobinjam.tempo.feature.badges.domain.allBadges
import com.mobinjam.tempo.feature.badges.presentation.BadgeIcon
import org.koin.compose.viewmodel.koinViewModel

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showFriends by remember { mutableStateOf(false) }

    if (showFriends) {
        com.mobinjam.tempo.feature.social.FriendsScreen()
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 40.dp,
            bottom = 100.dp,
        ),
    ) {
        item(span = { GridItemSpan(3) }) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Profile",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentBlue.copy(alpha = 0.15f))
                            .clickable { showFriends = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = "Friends",
                            color = AccentBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBg)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${state.unlockedBadgeIds.size} / ${allBadges.size}",
                            color = AccentBlue,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Badges unlocked",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatHours(state.totalHours),
                            color = AccentBlue,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Total studied",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Achievements",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(4.dp))
            }
        }

        items(allBadges) { badge ->
            BadgeCell(
                badge = badge,
                unlocked = state.unlockedBadgeIds.contains(badge.id),
            )
        }
    }
}

@Composable
private fun BadgeCell(
    badge: Badge,
    unlocked: Boolean,
) {
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
            // small check or lock marker at corner
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

private fun formatHours(hours: Double): String {
    return if (hours >= 1) {
        "${hours.toInt()}h"
    } else {
        "${(hours * 60).toInt()}m"
    }
}