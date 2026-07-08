package com.mobinjam.tempo.feature.study

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.feature.study.domain.CategoryTime
import com.mobinjam.tempo.feature.study.presentation.StudyViewModel
import com.mobinjam.tempo.feature.study.presentation.TimerStatus
import com.mobinjam.tempo.feature.tasks.domain.TaskCategory
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.KeyboardType

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)
private val StopRed = Color(0xFFE57373)

@Composable
fun StudyScreen(
    viewModel: StudyViewModel = koinViewModel(),
    studyLauncher: com.mobinjam.tempo.feature.main.StudyLauncher = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showGoalDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val pending = studyLauncher.consumeCategory()
        if (pending != null) {
            viewModel.startWithCategory(pending)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                onStart = viewModel::start,
                onPause = viewModel::pause,
                onResume = viewModel::resume,
                onStop = viewModel::stopAndSave,
            )

            if (state.errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = state.errorMessage!!,
                    color = StopRed,
                    fontSize = 13.sp,
                )
            }

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

            Spacer(Modifier.height(24.dp))

            DailyGoalCard(
                todaySeconds = state.stats.todaySeconds,
                goalMinutes = state.dailyGoalMinutes,
                progress = state.goalProgress,
                reached = state.goalReached,
                onEditGoal = { showGoalDialog = true },
            )

            Spacer(Modifier.height(24.dp))

            StatsRow(
                todaySeconds = state.stats.todaySeconds,
                weekSeconds = state.stats.weekSeconds,
                streakDays = state.stats.streakDays,
            )

            Spacer(Modifier.height(12.dp))

            WeekComparisonCard(
                thisWeek = state.stats.weekSeconds,
                lastWeek = state.stats.lastWeekSeconds,
            )

            if (state.bestHour != null) {
                Spacer(Modifier.height(12.dp))
                BestHourCard(
                    hour = state.bestHour!!.hour,
                    totalSeconds = state.bestHour!!.totalSeconds,
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Activity",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(12.dp))

            StudyHeatmap(
                dailyTotals = state.dailyTotals,
                dailyBreakdown = state.dailyBreakdown,
                selectedDate = state.selectedHeatmapDate,
                onDaySelected = viewModel::onHeatmapDaySelected,
            )

            Spacer(Modifier.height(30.dp))
        }

        // celebration overlay when goal reached
        if (state.goalReached) {
            CelebrationOverlay(onDismiss = viewModel::dismissCelebration)
        }
    }

    if (showGoalDialog) {
        GoalDialog(
            currentMinutes = state.dailyGoalMinutes,
            onConfirm = { minutes ->
                viewModel.setDailyGoal(minutes)
                showGoalDialog = false
            },
            onDismiss = { showGoalDialog = false },
        )
    }
}

@Composable
private fun DailyGoalCard(
    todaySeconds: Long,
    goalMinutes: Int,
    progress: Float,
    reached: Boolean,
    onEditGoal: () -> Unit,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "goalProgress",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Daily goal",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (reached) "Reached 🎉" else "${goalMinutes}m goal",
                color = if (reached) AccentBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Edit",
                color = AccentBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onEditGoal() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF20262E)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AccentBlue),
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${formatHoursMinutes(todaySeconds)} of ${goalMinutes}m",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun GoalDialog(
    currentMinutes: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf(30, 60, 90, 120, 180, 240)
    var selected by remember { mutableStateOf(currentMinutes) }
    var customText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF16181C))
                .padding(20.dp),
        ) {
            Text(
                text = "Set daily goal",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(16.dp))

            options.forEach { m ->
                val isSel = selected == m && customText.isBlank()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) AccentBlue.copy(alpha = 0.15f) else CardBg)
                        .then(
                            if (isSel) Modifier.border(1.dp, AccentBlue, RoundedCornerShape(12.dp))
                            else Modifier
                        )
                        .clickable {
                            selected = m
                            customText = ""
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = goalLabel(m),
                        color = if (isSel) AccentBlue else MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // custom minutes input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (customText.isNotBlank()) AccentBlue.copy(alpha = 0.15f) else CardBg)
                    .then(
                        if (customText.isNotBlank()) Modifier.border(1.dp, AccentBlue, RoundedCornerShape(12.dp))
                        else Modifier
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicTextField(
                    value = customText,
                    onValueChange = { input ->
                        customText = input.filter { it.isDigit() }.take(4)
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentBlue),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (customText.isEmpty()) {
                            Text(
                                "Custom minutes...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                            )
                        }
                        inner()
                    },
                )
                Text(
                    text = "min",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            val finalMinutes = customText.toIntOrNull()?.takeIf { it > 0 } ?: selected
            val isValid = finalMinutes > 0

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isValid) AccentBlue else Color(0xFF2A3040))
                    .clickable(enabled = isValid) { onConfirm(finalMinutes) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Save", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CelebrationOverlay(onDismiss: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, easing = LinearEasing),
        label = "celebrationScale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF16181C))
                .border(1.dp, AccentBlue, RoundedCornerShape(24.dp))
                .padding(horizontal = 32.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "🎉", fontSize = 56.sp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Goal reached!",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "You hit your study goal today.\nGreat work 👏",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentBlue)
                    .clickable { onDismiss() }
                    .padding(horizontal = 32.dp, vertical = 12.dp),
            ) {
                Text("Nice!", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun TimerCard(
    time: String,
    status: TimerStatus,
    category: String?,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
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

@Composable
private fun StatsRow(
    todaySeconds: Long,
    weekSeconds: Long,
    streakDays: Int,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            value = formatHoursMinutes(todaySeconds),
            label = "Today",
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = formatHoursMinutes(weekSeconds),
            label = "This week",
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = if (streakDays > 0) "$streakDays 🔥" else "0",
            label = "Day streak",
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            color = AccentBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
        )
    }
}

@Composable
private fun StudyHeatmap(
    dailyTotals: Map<String, Long>,
    dailyBreakdown: Map<String, List<CategoryTime>>,
    selectedDate: String?,
    onDaySelected: (String) -> Unit,
) {
    val weeks = 12
    val today = DateUtils.today()

    val startMonday = run {
        val mondayThisWeek = today.minus(today.dayOfWeek.ordinal, DateTimeUnit.DAY)
        mondayThisWeek.minus((weeks - 1) * 7, DateTimeUnit.DAY)
    }

    val columns = (0 until weeks).map { w ->
        (0 until 7).map { d ->
            startMonday.plus(w * 7 + d, DateTimeUnit.DAY)
        }
    }

    val weekdayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(16.dp),
    ) {
        val scroll = rememberScrollState()

        // month labels row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll),
        ) {
            Spacer(Modifier.width(38.dp))
            var lastMonth = -1
            columns.forEach { week ->
                val firstOfWeek = week.first()
                val label = if (firstOfWeek.monthNumber != lastMonth) {
                    lastMonth = firstOfWeek.monthNumber
                    monthShort(firstOfWeek.monthNumber)
                } else ""
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.width(20.dp),
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll),
        ) {
            // weekday labels column (all 7 days)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(end = 8.dp),
            ) {
                weekdayLabels.forEach { day ->
                    Box(
                        modifier = Modifier.size(width = 30.dp, height = 16.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = day,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp,
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                columns.forEach { week ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        week.forEach { date ->
                            val dateStr = DateUtils.toDbString(date)
                            val seconds = dailyTotals[dateStr] ?: 0L
                            val isFuture = date > today
                            HeatCell(
                                seconds = seconds,
                                isFuture = isFuture,
                                isSelected = selectedDate == dateStr,
                                onClick = { if (!isFuture) onDaySelected(dateStr) },
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Less",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
            )
            Spacer(Modifier.width(6.dp))
            listOf(0L, 600L, 1800L, 3600L, 7200L).forEach { s ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(heatColor(s)),
                )
                Spacer(Modifier.width(3.dp))
            }
            Spacer(Modifier.width(3.dp))
            Text(
                text = "More",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
            )
        }

        AnimatedVisibility(visible = selectedDate != null) {
            if (selectedDate != null) {
                Column {
                    Spacer(Modifier.height(14.dp))
                    DayInfoCard(
                        date = selectedDate,
                        breakdown = dailyBreakdown[selectedDate].orEmpty(),
                        totalSeconds = dailyTotals[selectedDate] ?: 0L,
                    )
                }
            }
        }
    }
}

@Composable
private fun DayInfoCard(
    date: String,
    breakdown: List<CategoryTime>,
    totalSeconds: Long,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(
            text = prettyDate(date),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(10.dp))

        if (breakdown.isEmpty()) {
            Text(
                text = "No study on this day",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        } else {
            breakdown.forEach { ct ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = ct.category,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 13.sp,
                    )
                    Text(
                        text = formatClock(ct.seconds),
                        color = AccentBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = formatClock(totalSeconds),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun HeatCell(
    seconds: Long,
    isFuture: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (isFuture) Color.Transparent else heatColor(seconds))
            .then(
                if (isSelected) Modifier.border(1.5.dp, Color.White, RoundedCornerShape(4.dp))
                else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    )
}

private fun heatColor(seconds: Long): Color {
    val minutes = seconds / 60
    return when {
        minutes <= 0 -> Color(0xFF20262E)
        minutes < 15 -> AccentBlue.copy(alpha = 0.25f)
        minutes < 45 -> AccentBlue.copy(alpha = 0.45f)
        minutes < 90 -> AccentBlue.copy(alpha = 0.70f)
        else -> AccentBlue
    }
}

private fun formatHoursMinutes(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "0m"
    }
}

private fun formatClock(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return if (hours > 0) "$hours:${twoDigits(minutes)}" else "0:${twoDigits(minutes)}"
}

private fun twoDigits(v: Long): String = if (v < 10) "0$v" else v.toString()

private fun monthShort(month: Int): String =
    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        .getOrElse(month - 1) { "" }

private fun prettyDate(dateStr: String): String {
    val date = DateUtils.fromDbString(dateStr) ?: return dateStr
    return "${monthShort(date.monthNumber)} ${date.dayOfMonth}, ${date.year}"
}

private fun goalLabel(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return when {
        h > 0 && m > 0 -> "${h}h ${m}m"
        h > 0 -> "${h}h"
        else -> "${m}m"
    }

}
@Composable
private fun WeekComparisonCard(
    thisWeek: Long,
    lastWeek: Long,
) {
    // figure out the percentage change vs last week
    val (message, isUp, showArrow) = when {
        lastWeek == 0L && thisWeek == 0L ->
            Triple("No study data yet this week", false, false)
        lastWeek == 0L && thisWeek > 0L ->
            Triple("Great start! Nothing to compare yet", true, false)
        else -> {
            val diff = thisWeek - lastWeek
            val percent = ((diff.toFloat() / lastWeek) * 100).toInt()
            when {
                percent > 0 -> Triple("You studied $percent% more than last week", true, true)
                percent < 0 -> Triple("You studied ${-percent}% less than last week", false, true)
                else -> Triple("Same as last week — steady!", true, false)
            }
        }
    }

    val accent = if (isUp) AccentBlue else Color(0xFFE57373)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showArrow) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isUp) "↑" else "↓",
                    color = accent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(14.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "This week vs last week",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${formatHoursMinutes(thisWeek)}  vs  ${formatHoursMinutes(lastWeek)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun BestHourCard(
    hour: Int,
    totalSeconds: Long,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AccentBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = hourEmoji(hour), fontSize = 20.sp)
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Your focus time",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "You focus best around ${formatHour(hour)}",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${formatHoursMinutes(totalSeconds)} studied at this hour",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
    }
}

private fun formatHour(hour: Int): String {
    return when {
        hour == 0 -> "12 AM"
        hour < 12 -> "$hour AM"
        hour == 12 -> "12 PM"
        else -> "${hour - 12} PM"
    }
}

private fun hourEmoji(hour: Int): String {
    return when (hour) {
        in 5..11 -> "🌅"
        in 12..16 -> "☀️"
        in 17..20 -> "🌇"
        else -> "🌙"
    }
}