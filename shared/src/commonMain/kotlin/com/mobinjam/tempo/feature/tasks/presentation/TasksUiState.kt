package com.mobinjam.tempo.feature.tasks.presentation

import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.feature.tasks.domain.Task
import com.mobinjam.tempo.feature.tasks.domain.TaskPriority
import kotlinx.datetime.LocalDate

data class TasksUiState(
    val allTasks: List<Task> = emptyList(),
    val selectedDate: LocalDate = DateUtils.today(),
    val isLoading: Boolean = false,
    val hasLoadedOnce: Boolean = false,
    val errorMessage: String? = null,
    val isAddingTask: Boolean = false,
    val newTaskTitle: String = "",
    val newTaskDescription: String = "",
    val newTaskPriority: TaskPriority = TaskPriority.MEDIUM,
    val newTaskCategory: String? = null,
) {
    val tasksForSelectedDate: List<Task>
        get() = allTasks.filter { it.dueDate == DateUtils.toDbString(selectedDate) }

    val completedCount: Int
        get() = tasksForSelectedDate.count { it.isDone }

    val totalCount: Int
        get() = tasksForSelectedDate.size

    val progress: Float
        get() = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
}