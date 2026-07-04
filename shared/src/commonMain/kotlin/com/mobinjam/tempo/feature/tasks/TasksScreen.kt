package com.mobinjam.tempo.feature.tasks

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mobinjam.tempo.feature.tasks.domain.Task
import com.mobinjam.tempo.feature.tasks.presentation.TasksViewModel
import org.koin.compose.viewmodel.koinViewModel

private val AccentBlue = Color(0xFF3AC6FF)

@Composable
fun TasksScreen(
    viewModel: TasksViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = "My Tasks",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(20.dp))

        // ---- add new task row ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = state.newTaskTitle,
                onValueChange = viewModel::onNewTaskTitleChange,
                placeholder = {
                    Text("Add a new task...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1A1F2E),
                    unfocusedContainerColor = Color(0xFF1A1F2E),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AccentBlue,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                ),
            )

            Spacer(Modifier.size(10.dp))

            // add button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentBlue)
                    .clickable { viewModel.addTask() },
                contentAlignment = Alignment.Center,
            ) {
                if (state.isAddingTask) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("+", color = Color.White, fontSize = 26.sp)
                }
            }
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

        // ---- task list ----
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = AccentBlue)
                }
            }

            state.tasks.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No tasks yet. Add your first one!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(state.tasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggle = { viewModel.toggleTask(task.id, task.isDone) },
                            onDelete = { viewModel.deleteTask(task.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1F2E))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // check circle
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (task.isDone) AccentBlue else Color(0xFF2A3040))
                .clickable { onToggle() },
            contentAlignment = Alignment.Center,
        ) {
            if (task.isDone) {
                Text("✓", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.size(14.dp))

        // title
        Text(
            text = task.title,
            color = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f),
        )

        // delete button
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