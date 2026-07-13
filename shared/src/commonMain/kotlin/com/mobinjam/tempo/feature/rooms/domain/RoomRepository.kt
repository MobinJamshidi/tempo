package com.mobinjam.tempo.feature.rooms.domain

interface RoomRepository {

    // rooms the current user is a member of
    suspend fun getMyRooms(): Result<List<Room>>

    // create a room and add the creator as a member
    // add a friend to a room
    suspend fun addMember(roomId: Long, userId: String): Result<Unit>

    // leave a room (or remove a member if owner)
    suspend fun removeMember(roomId: Long, userId: String): Result<Unit>

    // delete a room (owner only)
    suspend fun deleteRoom(roomId: Long): Result<Unit>

    // get members of a room with their live study status
    suspend fun getRoomMembers(roomId: Long): Result<List<RoomMember>>

    // tasks in a room, with subtasks and who completed them
    suspend fun getRoomTasks(roomId: Long): Result<List<RoomTask>>

    suspend fun addRoomTask(roomId: Long, title: String): Result<Unit>

    suspend fun addRoomSubtask(taskId: Long, title: String): Result<Unit>

    suspend fun deleteRoomTask(taskId: Long): Result<Unit>

    // toggle my completion of a task or subtask
    suspend fun toggleTaskCompletion(taskId: Long, currentlyDone: Boolean): Result<Unit>

    suspend fun toggleSubtaskCompletion(subtaskId: Long, currentlyDone: Boolean): Result<Unit>

    suspend fun createRoom(name: String, icon: Int): Result<Long>

}