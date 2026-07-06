package com.mobinjam.tempo.feature.tasks.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Long = 0,
    val title: String,
    @SerialName("is_done") val isDone: Boolean = false,
    @SerialName("due_date") val dueDate: String? = null,
    val priority: String = "medium",
    val description: String? = null,
    val category: String? = null,
)