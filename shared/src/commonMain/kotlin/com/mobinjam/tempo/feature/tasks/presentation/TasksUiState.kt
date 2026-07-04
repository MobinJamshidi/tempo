package com.mobinjam.tempo.feature.tasks.presentation

import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.feature.tasks.domain.Task
import kotlinx.datetime.LocalDate

data class TasksUiState(
    val allTasks: List<Task> = emptyList(),
    val selectedDate: LocalDate = DateUtils.today(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val newTaskTitle: String = "",
    val isAddingTask: Boolean = false,
) {
    // tasks that belong to the selected day (computed automatically)
    val tasksForSelectedDate: List<Task>
        get() = allTasks.filter { it.dueDate == DateUtils.toDbString(selectedDate) }
}