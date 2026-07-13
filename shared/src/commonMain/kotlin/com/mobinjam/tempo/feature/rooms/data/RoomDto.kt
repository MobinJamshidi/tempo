package com.mobinjam.tempo.feature.rooms.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    val id: Long,
    val name: String,
    @SerialName("owner_id") val ownerId: String,
    val icon: Int = 0,
)

@Serializable
data class NewRoomDto(
    val name: String,
    @SerialName("owner_id") val ownerId: String,
    val icon: Int = 0,
)
@Serializable
data class RoomMemberDto(
    val id: Long = 0,
    @SerialName("room_id") val roomId: Long,
    @SerialName("user_id") val userId: String,
)

@Serializable
data class NewMemberDto(
    @SerialName("room_id") val roomId: Long,
    @SerialName("user_id") val userId: String,
)