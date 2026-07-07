package com.mobinjam.tempo.feature.tasks.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubtaskDto(
    val id: Long = 0,
    @SerialName("task_id") val taskId: Long,
    val title: String,
    @SerialName("is_done") val isDone: Boolean = false,
)