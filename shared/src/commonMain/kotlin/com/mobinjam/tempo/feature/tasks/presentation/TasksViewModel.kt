package com.mobinjam.tempo.feature.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.feature.tasks.domain.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class TasksViewModel(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            taskRepository.getTasks().fold(
                onSuccess = { tasks ->
                    _uiState.update { it.copy(isLoading = false, allTasks = tasks) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Failed to load tasks")
                    }
                },
            )
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun onNewTaskTitleChange(value: String) {
        _uiState.update { it.copy(newTaskTitle = value) }
    }

    fun addTask() {
        val title = _uiState.value.newTaskTitle.trim()
        if (title.isBlank()) return

        val date = DateUtils.toDbString(_uiState.value.selectedDate)

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingTask = true, errorMessage = null) }

            taskRepository.addTask(title = title, dueDate = date).fold(
                onSuccess = {
                    _uiState.update { it.copy(isAddingTask = false, newTaskTitle = "") }
                    loadTasks()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isAddingTask = false, errorMessage = error.message ?: "Failed to add task")
                    }
                },
            )
        }
    }

    fun toggleTask(id: Long, currentIsDone: Boolean) {
        viewModelScope.launch {
            taskRepository.toggleTaskDone(id, !currentIsDone).fold(
                onSuccess = { loadTasks() },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to update task") }
                },
            )
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            taskRepository.deleteTask(id).fold(
                onSuccess = { loadTasks() },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message ?: "Failed to delete task") }
                },
            )
        }
    }
}