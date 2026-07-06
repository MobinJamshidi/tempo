package com.mobinjam.tempo.feature.tasks

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.feature.tasks.domain.Task
import com.mobinjam.tempo.feature.tasks.domain.TaskPriority
import com.mobinjam.tempo.feature.tasks.presentation.AddTaskSheet
import com.mobinjam.tempo.feature.tasks.presentation.MonthCalendarDialog
import com.mobinjam.tempo.feature.tasks.presentation.TasksViewModel
import com.mobinjam.tempo.feature.tasks.presentation.WeekStrip
import com.mobinjam.tempo.feature.tasks.presentation.priorityColor
import org.koin.compose.viewmodel.koinViewModel

private val AccentBlue = Color(0xFF3AC6FF)
private val CardBg = Color(0xFF1A1F2E)

@Composable
fun TasksScreen(
    viewModel: TasksViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCalendar by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hi 👋",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                    )
                    Text(
                        text = "Let's get things done",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            WeekStrip(
                selectedDate = state.selectedDate,
                onDateSelected = viewModel::onDateSelected,
                onCalendarClick = { showCalendar = true },
            )

            if (state.totalCount > 0) {
                Spacer(Modifier.height(16.dp))
                DayProgress(
                    completed = state.completedCount,
                    total = state.totalCount,
                    progress = state.progress,
                )
            }

            Spacer(Modifier.height(20.dp))

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = Color(0xFFE57373),
                    fontSize = 13.sp,
                )
                Spacer(Modifier.height(12.dp))
            }

            val tasks = state.tasksForSelectedDate
            when {
                state.isLoading || !state.hasLoadedOnce -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = AccentBlue)
                    }
                }

                tasks.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().height(240.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = "🗒️", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "No tasks yet",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Tap + to add your first task for this day",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp),
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            TaskCard(
                                task = task,
                                onToggle = { viewModel.toggleTask(task.id, task.isDone) },
                                onDelete = { viewModel.deleteTask(task.id) },
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .padding(bottom = 80.dp)
                .size(60.dp)
                .clip(CircleShape)
                .background(AccentBlue)
                .clickable { showAddSheet = true },
            contentAlignment = Alignment.Center,
        ) {
            Text("+", color = Color.White, fontSize = 30.sp)
        }
    }

    if (showCalendar) {
        MonthCalendarDialog(
            selectedDate = state.selectedDate,
            onDateSelected = viewModel::onDateSelected,
            onDismiss = { showCalendar = false },
        )
    }

    if (showAddSheet) {
        AddTaskSheet(
            title = state.newTaskTitle,
            description = state.newTaskDescription,
            priority = state.newTaskPriority,
            selectedCategory = state.newTaskCategory,
            isAdding = state.isAddingTask,
            onTitleChange = viewModel::onNewTaskTitleChange,
            onDescriptionChange = viewModel::onNewTaskDescriptionChange,
            onPriorityChange = viewModel::onNewTaskPriorityChange,
            onCategoryChange = viewModel::onNewTaskCategoryChange,
            onConfirm = {
                viewModel.addTask()
                showAddSheet = false
            },
            onDismiss = { showAddSheet = false },
        )
    }
}

@Composable
private fun DayProgress(
    completed: Int,
    total: Int,
    progress: Float,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400),
        label = "dayProgress",
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$completed of $total tasks done",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                color = AccentBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(CardBg),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AccentBlue),
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val circleColor by animateColorAsState(
            targetValue = if (task.isDone) AccentBlue else Color(0xFF2A3040),
            animationSpec = tween(durationMillis = 300),
            label = "checkColor",
        )
        val checkScale by animateFloatAsState(
            targetValue = if (task.isDone) 1f else 0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "checkScale",
        )

        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(circleColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggle,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "✓",
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = checkScale
                    scaleY = checkScale
                },
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
            )

            if (!task.description.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = task.description!!,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PriorityTag(task.priority)

                if (!task.category.isNullOrBlank()) {
                    Text(
                        text = task.category!!,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        Text(
            text = "✕",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onDelete() }
                .padding(4.dp),
        )
    }
}

@Composable
private fun PriorityTag(priority: TaskPriority) {
    val color = priorityColor(priority)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = priority.label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}