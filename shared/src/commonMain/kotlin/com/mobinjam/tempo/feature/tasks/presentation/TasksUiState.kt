package com.mobinjam.tempo.feature.tasks.presentation

import com.mobinjam.tempo.feature.tasks.domain.Task

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val newTaskTitle: String = "",
    val isAddingTask: Boolean = false,
)