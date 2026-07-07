package com.mobinjam.tempo.feature.tasks.domain

interface TaskRepository {

    suspend fun getTasks(): Result<List<Task>>

    suspend fun addTask(
        title: String,
        dueDate: String?,
        priority: String,
        description: String?,
        category: String?,
    ): Result<Unit>

    suspend fun updateTask(
        id: Long,
        title: String,
        priority: String,
        description: String?,
        category: String?,
    ): Result<Unit>

    suspend fun toggleTaskDone(id: Long, isDone: Boolean): Result<Unit>

    suspend fun deleteTask(id: Long): Result<Unit>

    suspend fun getSubtasks(): Result<List<Subtask>>

    suspend fun addSubtask(taskId: Long, title: String): Result<Unit>

    suspend fun toggleSubtaskDone(id: Long, isDone: Boolean): Result<Unit>

    suspend fun deleteSubtask(id: Long): Result<Unit>
}