package com.mobinjam.tempo.feature.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.feature.study.presentation.StudyViewModel
import com.mobinjam.tempo.feature.study.presentation.TimerStatus
import com.mobinjam.tempo.feature.tasks.domain.TaskCategory
import org.koin.compose.viewmodel.koinViewModel

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)
private val StopRed = Color(0xFFE57373)

@Composable
fun StudyScreen(
    viewModel: StudyViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = "Study",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(20.dp))

        TimerCard(
            time = state.formattedTime,
            status = state.status,
            category = state.selectedCategory,
            isSaving = state.isSaving,
            onStart = viewModel::start,
            onPause = viewModel::pause,
            onResume = viewModel::resume,
            onStop = viewModel::stopAndSave,
            onCancel = viewModel::cancelTimer,
        )

        if (state.errorMessage != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = state.errorMessage!!,
                color = StopRed,
                fontSize = 13.sp,
            )
        }

        // category picker only shown when timer is idle
        AnimatedVisibility(visible = state.status == TimerStatus.IDLE) {
            Column {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "What are you studying?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TaskCategory.entries.forEach { cat ->
                        CategoryChip(
                            label = "${cat.icon} ${cat.label}",
                            isSelected = state.selectedCategory == cat.label,
                            onClick = { viewModel.onCategorySelected(cat.label) },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))

        // placeholder for upcoming features (heatmap, streak, stats...)
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "📊 Stats coming soon",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun TimerCard(
    time: String,
    status: TimerStatus,
    category: String?,
    isSaving: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onCancel: () -> Unit,
) {
    val isActive = status == TimerStatus.RUNNING || status == TimerStatus.PAUSED

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(CardBg)
            .then(
                if (isActive) Modifier.border(2.dp, AccentBlue, RoundedCornerShape(28.dp))
                else Modifier
            )
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // left button: play / pause
            CircleButton(
                bg = if (status == TimerStatus.RUNNING) Color(0xFF2A3040) else AccentBlue,
                content = {
                    Text(
                        text = if (status == TimerStatus.RUNNING) "⏸" else "▶",
                        color = Color.White,
                        fontSize = 20.sp,
                    )
                },
                onClick = {
                    when (status) {
                        TimerStatus.IDLE -> onStart()
                        TimerStatus.RUNNING -> onPause()
                        TimerStatus.PAUSED -> onResume()
                    }
                },
            )

            // center: time + label
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = time,
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = when {
                        status == TimerStatus.PAUSED -> "Paused"
                        category != null -> category
                        else -> "Study timer"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }

            // right button: stop (only when active)
            if (isActive) {
                CircleButton(
                    bg = StopRed,
                    content = {
                        Text(text = "✕", color = Color.White, fontSize = 20.sp)
                    },
                    onClick = onStop,
                )
            } else {
                Spacer(Modifier.size(56.dp))
            }
        }
    }
}

@Composable
private fun CircleButton(
    bg: Color,
    content: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else CardBg)
            .then(
                if (isSelected) Modifier.border(1.dp, AccentBlue, RoundedCornerShape(50)) else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            color = if (isSelected) AccentBlue else MaterialTheme.colorScheme.onBackground,
            fontSize = 13.sp,
        )
    }
}