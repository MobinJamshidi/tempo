package com.mobinjam.tempo.feature.tasks.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.feature.tasks.domain.Task
import com.mobinjam.tempo.feature.tasks.domain.TaskPriority
import com.mobinjam.tempo.feature.tasks.domain.TaskRepository
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import com.mobinjam.tempo.feature.tasks.domain.Subtask


class SupabaseTaskRepository : TaskRepository {

    private val db = SupabaseClientProvider.client.postgrest

    override suspend fun getTasks(): Result<List<Task>> =
        runCatching {
            val result = db.from("tasks")
                .select {
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<TaskDto>()

            result.map { dto ->
                Task(
                    id = dto.id,
                    title = dto.title,
                    isDone = dto.isDone,
                    dueDate = dto.dueDate,
                    priority = TaskPriority.fromDb(dto.priority),
                    description = dto.description,
                    category = dto.category,
                )
            }
        }

    override suspend fun addTask(
        title: String,
        dueDate: String?,
        priority: String,
        description: String?,
        category: String?,
    ): Result<Unit> =
        runCatching {
            db.from("tasks").insert(
                TaskDto(
                    title = title,
                    dueDate = dueDate,
                    priority = priority,
                    description = description,
                    category = category,
                )
            )
            Unit
        }

    override suspend fun updateTask(
        id: Long,
        title: String,
        priority: String,
        description: String?,
        category: String?,
    ): Result<Unit> =
        runCatching {
            db.from("tasks").update(
                {
                    set("title", title)
                    set("priority", priority)
                    set("description", description)
                    set("category", category)
                }
            ) {
                filter { eq("id", id) }
            }
            Unit
        }

    override suspend fun toggleTaskDone(id: Long, isDone: Boolean): Result<Unit> =
        runCatching {
            db.from("tasks").update(
                { set("is_done", isDone) }
            ) {
                filter { eq("id", id) }
            }
            Unit
        }

    override suspend fun deleteTask(id: Long): Result<Unit> =
        runCatching {
            db.from("tasks").delete {
                filter { eq("id", id) }
            }
            Unit
        }

    override suspend fun getSubtasks(): Result<List<Subtask>> =
        runCatching {
            val result = db.from("subtasks")
                .select {
                    order("created_at", Order.ASCENDING)
                }
                .decodeList<SubtaskDto>()

            result.map { dto ->
                Subtask(
                    id = dto.id,
                    taskId = dto.taskId,
                    title = dto.title,
                    isDone = dto.isDone,
                )
            }
        }

    override suspend fun addSubtask(taskId: Long, title: String): Result<Unit> =
        runCatching {
            db.from("subtasks").insert(
                SubtaskDto(taskId = taskId, title = title)
            )
            Unit
        }

    override suspend fun toggleSubtaskDone(id: Long, isDone: Boolean): Result<Unit> =
        runCatching {
            db.from("subtasks").update(
                { set("is_done", isDone) }
            ) {
                filter { eq("id", id) }
            }
            Unit
        }

    override suspend fun deleteSubtask(id: Long): Result<Unit> =
        runCatching {
            db.from("subtasks").delete {
                filter { eq("id", id) }
            }
            Unit
        }
}