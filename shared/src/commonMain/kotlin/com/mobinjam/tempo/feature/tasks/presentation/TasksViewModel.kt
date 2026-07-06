package com.mobinjam.tempo.feature.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.feature.tasks.domain.Task
import com.mobinjam.tempo.feature.tasks.domain.TaskPriority
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
                    _uiState.update {
                        it.copy(isLoading = false, allTasks = tasks, hasLoadedOnce = true)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasLoadedOnce = true,
                            errorMessage = error.message ?: "Failed to load tasks",
                        )
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

    fun onNewTaskDescriptionChange(value: String) {
        _uiState.update { it.copy(newTaskDescription = value) }
    }

    fun onNewTaskPriorityChange(priority: TaskPriority) {
        _uiState.update { it.copy(newTaskPriority = priority) }
    }

    fun onNewTaskCategoryChange(category: String?) {
        _uiState.update { it.copy(newTaskCategory = category) }
    }

    fun addTask() {
        val current = _uiState.value
        val title = current.newTaskTitle.trim()
        if (title.isBlank()) return

        val date = DateUtils.toDbString(current.selectedDate)
        val description = current.newTaskDescription.trim().ifBlank { null }

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingTask = true, errorMessage = null) }

            taskRepository.addTask(
                title = title,
                dueDate = date,
                priority = current.newTaskPriority.dbValue,
                description = description,
                category = current.newTaskCategory,
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isAddingTask = false,
                            newTaskTitle = "",
                            newTaskDescription = "",
                            newTaskPriority = TaskPriority.MEDIUM,
                            newTaskCategory = null,
                        )
                    }
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

    fun startEditingTask(task: Task) {
        _uiState.update {
            it.copy(
                editingTaskId = task.id,
                newTaskTitle = task.title,
                newTaskDescription = task.description ?: "",
                newTaskPriority = task.priority,
                newTaskCategory = task.category,
            )
        }
    }

    fun cancelEditing() {
        _uiState.update {
            it.copy(
                editingTaskId = null,
                newTaskTitle = "",
                newTaskDescription = "",
                newTaskPriority = TaskPriority.MEDIUM,
                newTaskCategory = null,
            )
        }
    }

    fun saveTask() {
        val editingId = _uiState.value.editingTaskId
        if (editingId == null) {
            addTask()
        } else {
            updateEditingTask(editingId)
        }
    }

    private fun updateEditingTask(id: Long) {
        val current = _uiState.value
        val title = current.newTaskTitle.trim()
        if (title.isBlank()) return

        val description = current.newTaskDescription.trim().ifBlank { null }

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingTask = true, errorMessage = null) }

            taskRepository.updateTask(
                id = id,
                title = title,
                priority = current.newTaskPriority.dbValue,
                description = description,
                category = current.newTaskCategory,
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isAddingTask = false,
                            editingTaskId = null,
                            newTaskTitle = "",
                            newTaskDescription = "",
                            newTaskPriority = TaskPriority.MEDIUM,
                            newTaskCategory = null,
                        )
                    }
                    loadTasks()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isAddingTask = false, errorMessage = error.message ?: "Failed to update task")
                    }
                },
            )
        }
    }

    fun toggleTask(id: Long, currentIsDone: Boolean) {
        val newValue = !currentIsDone

        _uiState.update { state ->
            state.copy(
                allTasks = state.allTasks.map { task ->
                    if (task.id == id) task.copy(isDone = newValue) else task
                }
            )
        }

        viewModelScope.launch {
            taskRepository.toggleTaskDone(id, newValue).fold(
                onSuccess = { },
                onFailure = { error ->
                    _uiState.update { state ->
                        state.copy(
                            allTasks = state.allTasks.map { task ->
                                if (task.id == id) task.copy(isDone = currentIsDone) else task
                            },
                            errorMessage = error.message ?: "Failed to update task",
                        )
                    }
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