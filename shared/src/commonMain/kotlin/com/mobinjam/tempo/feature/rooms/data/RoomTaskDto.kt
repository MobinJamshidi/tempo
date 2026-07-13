package com.mobinjam.tempo.feature.rooms.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomTaskDto(
    val id: Long,
    @SerialName("room_id") val roomId: Long,
    val title: String,
    @SerialName("created_by") val createdBy: String,
)

@Serializable
data class NewRoomTaskDto(
    @SerialName("room_id") val roomId: Long,
    val title: String,
    @SerialName("created_by") val createdBy: String,
)

@Serializable
data class RoomSubtaskDto(
    val id: Long,
    @SerialName("task_id") val taskId: Long,
    val title: String,
)

@Serializable
data class NewRoomSubtaskDto(
    @SerialName("task_id") val taskId: Long,
    val title: String,
)

@Serializable
data class RoomCompletionDto(
    val id: Long = 0,
    @SerialName("user_id") val userId: String,
    @SerialName("task_id") val taskId: Long? = null,
    @SerialName("subtask_id") val subtaskId: Long? = null,
)

@Serializable
data class NewCompletionDto(
    @SerialName("user_id") val userId: String,
    @SerialName("task_id") val taskId: Long? = null,
    @SerialName("subtask_id") val subtaskId: Long? = null,
)