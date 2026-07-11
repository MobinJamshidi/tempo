package com.mobinjam.tempo.feature.social

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.feature.social.domain.ActiveFriend
import com.mobinjam.tempo.feature.social.presentation.GlobalStudyViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)
private val LiveGreen = Color(0xFF66BB6A)

@Composable
fun GlobalStudyScreen(
    onBack: () -> Unit,
    viewModel: GlobalStudyViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // ticks every second so live timers update
    var tick by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            tick++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    .padding(horizontal = 10.dp),
            )
            Spacer(Modifier.size(6.dp))
            Text(
                text = "Studying now",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(LiveGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(LiveGreen),
                )
                Spacer(Modifier.size(6.dp))
                Text(
                    text = "${state.activeUsers.size}",
                    color = LiveGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        if (state.activeUsers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No one is studying right now.\nBe the first! 🚀",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
            }
        } else {
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp),
            ) {
                items(state.activeUsers, key = { it.profile.id }) { user ->
                    GlobalUserCard(user = user, tick = tick)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun GlobalUserCard(user: ActiveFriend, tick: Long) {
    // live total = completed sessions today + current running session
    val currentSessionSeconds = remember(tick) { DateUtils.secondsSince(user.startedAt) }
    val liveTotal = user.todaySecondsBefore + currentSessionSeconds

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AccentBlue.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = user.profile.username.take(1).uppercase(),
                    color = AccentBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(LiveGreen)
                    .align(Alignment.BottomEnd),
            )
        }

        Spacer(Modifier.size(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.profile.username,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (user.category != null) "Studying ${user.category}" else "Studying",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatLiveTime(liveTotal),
                color = AccentBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "today",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
            )
        }
    }
}

private fun formatLiveTime(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "${hours}:${pad(minutes)}:${pad(seconds)}"
    } else {
        "${minutes}:${pad(seconds)}"
    }
}

private fun pad(v: Long): String = if (v < 10) "0$v" else v.toString()