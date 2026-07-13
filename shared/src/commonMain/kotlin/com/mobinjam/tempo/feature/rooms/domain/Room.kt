package com.mobinjam.tempo.feature.rooms.domain

import com.mobinjam.tempo.feature.social.domain.Profile


data class RoomMember(
    val profile: Profile,
    val isStudying: Boolean,
    val category: String?,
    val startedAt: String?,
    val todaySecondsBefore: Long = 0,
)

data class RoomTask(
    val id: Long,
    val title: String,
    val createdBy: String,
    val subtasks: List<RoomSubtask> = emptyList(),
    val completedBy: List<Profile> = emptyList(),
)

data class RoomSubtask(
    val id: Long,
    val title: String,
    val completedBy: List<Profile> = emptyList(),
)
data class Room(
    val id: Long,
    val name: String,
    val ownerId: String,
    val memberCount: Int = 0,
    val icon: Int = 0,
)