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

    suspend fun toggleTaskDone(id: Long, isDone: Boolean): Result<Unit>

    suspend fun deleteTask(id: Long): Result<Unit>
}