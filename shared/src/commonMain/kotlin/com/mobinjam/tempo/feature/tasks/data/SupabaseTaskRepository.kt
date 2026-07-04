package com.mobinjam.tempo.feature.tasks.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.feature.tasks.domain.Task
import com.mobinjam.tempo.feature.tasks.domain.TaskRepository
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

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
                )
            }
        }

    override suspend fun addTask(title: String, dueDate: String?): Result<Unit> =
        runCatching {
            db.from("tasks").insert(
                TaskDto(title = title, dueDate = dueDate)
            )
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
}