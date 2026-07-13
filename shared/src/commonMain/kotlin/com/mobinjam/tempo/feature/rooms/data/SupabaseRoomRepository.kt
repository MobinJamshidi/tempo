package com.mobinjam.tempo.feature.rooms.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.feature.rooms.domain.Room
import com.mobinjam.tempo.feature.rooms.domain.RoomMember
import com.mobinjam.tempo.feature.rooms.domain.RoomRepository
import com.mobinjam.tempo.feature.rooms.domain.RoomSubtask
import com.mobinjam.tempo.feature.rooms.domain.RoomTask
import com.mobinjam.tempo.feature.social.data.ActiveSessionDto
import com.mobinjam.tempo.feature.social.data.ProfileDto
import com.mobinjam.tempo.feature.social.domain.Profile
import com.mobinjam.tempo.feature.study.data.StudySessionDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest

class SupabaseRoomRepository : RoomRepository {

    private val db = SupabaseClientProvider.client.postgrest

    private fun myId(): String =
        SupabaseClientProvider.client.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("Not logged in")

    override suspend fun getMyRooms(): Result<List<Room>> =
        runCatching {
            val me = myId()

            val myMemberships = db.from("room_members")
                .select()
                .decodeList<RoomMemberDto>()
                .filter { it.userId == me }

            if (myMemberships.isEmpty()) return@runCatching emptyList()

            val roomIds = myMemberships.map { it.roomId }.distinct()

            val rooms = db.from("rooms")
                .select {
                    filter { isIn("id", roomIds) }
                }
                .decodeList<RoomDto>()

            val allMembers = db.from("room_members")
                .select()
                .decodeList<RoomMemberDto>()
                .filter { it.roomId in roomIds }

            val counts = allMembers.groupingBy { it.roomId }.eachCount()

            rooms.map { r ->
                Room(
                    id = r.id,
                    name = r.name,
                    ownerId = r.ownerId,
                    memberCount = counts[r.id] ?: 0,
                    icon = r.icon,
                )
            }
        }

    override suspend fun createRoom(name: String, icon: Int): Result<Long> =
        runCatching {
            val me = myId()

            val created = db.from("rooms").insert(
                NewRoomDto(name = name, ownerId = me, icon = icon)
            ) {
                select()
            }.decodeSingle<RoomDto>()

            db.from("room_members").insert(
                NewMemberDto(roomId = created.id, userId = me)
            )

            created.id
        }
    override suspend fun addMember(roomId: Long, userId: String): Result<Unit> =
        runCatching {
            db.from("room_members").insert(
                NewMemberDto(roomId = roomId, userId = userId)
            )
            Unit
        }

    override suspend fun removeMember(roomId: Long, userId: String): Result<Unit> =
        runCatching {
            db.from("room_members").delete {
                filter {
                    eq("room_id", roomId)
                    eq("user_id", userId)
                }
            }
            Unit
        }

    override suspend fun deleteRoom(roomId: Long): Result<Unit> =
        runCatching {
            db.from("rooms").delete {
                filter { eq("id", roomId) }
            }
            Unit
        }

    override suspend fun getRoomMembers(roomId: Long): Result<List<RoomMember>> =
        runCatching {
            val members = db.from("room_members")
                .select()
                .decodeList<RoomMemberDto>()
                .filter { it.roomId == roomId }

            if (members.isEmpty()) return@runCatching emptyList()

            val memberIds = members.map { it.userId }

            val profiles = db.from("profiles")
                .select {
                    filter { isIn("id", memberIds) }
                }
                .decodeList<ProfileDto>()
                .associateBy { it.id }

            val activeSessions = db.from("active_sessions")
                .select()
                .decodeList<ActiveSessionDto>()
                .filter { it.userId in memberIds }
                .associateBy { it.userId }

            val today = DateUtils.toDbString(DateUtils.today())
            val todaySessions = db.from("study_sessions")
                .select {
                    filter {
                        isIn("user_id", memberIds)
                        eq("date", today)
                    }
                }
                .decodeList<StudySessionDto>()

            val todayTotals = todaySessions
                .groupBy { it.userId }
                .mapValues { (_, list) -> list.sumOf { it.durationSeconds } }

            memberIds.mapNotNull { uid ->
                val p = profiles[uid] ?: return@mapNotNull null
                val active = activeSessions[uid]
                RoomMember(
                    profile = Profile(
                        id = p.id,
                        username = p.username,
                        displayName = p.displayName,
                    ),
                    isStudying = active != null,
                    category = active?.category,
                    startedAt = active?.startedAt,
                    todaySecondsBefore = todayTotals[uid] ?: 0L,
                )
            }
        }

    override suspend fun getRoomTasks(roomId: Long): Result<List<RoomTask>> =
        runCatching {
            val tasks = db.from("room_tasks")
                .select {
                    filter { eq("room_id", roomId) }
                }
                .decodeList<RoomTaskDto>()

            if (tasks.isEmpty()) return@runCatching emptyList()

            val taskIds = tasks.map { it.id }

            val subtasks = db.from("room_subtasks")
                .select {
                    filter { isIn("task_id", taskIds) }
                }
                .decodeList<RoomSubtaskDto>()

            val subtaskIds = subtasks.map { it.id }

            val completions = db.from("room_completions")
                .select()
                .decodeList<RoomCompletionDto>()
                .filter { c ->
                    (c.taskId != null && c.taskId in taskIds) ||
                            (c.subtaskId != null && c.subtaskId in subtaskIds)
                }

            val userIds = completions.map { it.userId }.distinct()
            val profiles = if (userIds.isEmpty()) {
                emptyMap()
            } else {
                db.from("profiles")
                    .select {
                        filter { isIn("id", userIds) }
                    }
                    .decodeList<ProfileDto>()
                    .associateBy { it.id }
            }

            fun profilesFor(ids: List<String>): List<Profile> =
                ids.mapNotNull { uid ->
                    profiles[uid]?.let {
                        Profile(id = it.id, username = it.username, displayName = it.displayName)
                    }
                }

            tasks.map { t ->
                val taskCompleters = completions
                    .filter { it.taskId == t.id && it.subtaskId == null }
                    .map { it.userId }

                val taskSubtasks = subtasks.filter { it.taskId == t.id }.map { s ->
                    val subCompleters = completions
                        .filter { it.subtaskId == s.id }
                        .map { it.userId }
                    RoomSubtask(
                        id = s.id,
                        title = s.title,
                        completedBy = profilesFor(subCompleters),
                    )
                }

                RoomTask(
                    id = t.id,
                    title = t.title,
                    createdBy = t.createdBy,
                    subtasks = taskSubtasks,
                    completedBy = profilesFor(taskCompleters),
                )
            }
        }

    override suspend fun addRoomTask(roomId: Long, title: String): Result<Unit> =
        runCatching {
            val me = myId()
            db.from("room_tasks").insert(
                NewRoomTaskDto(roomId = roomId, title = title, createdBy = me)
            )
            Unit
        }

    override suspend fun addRoomSubtask(taskId: Long, title: String): Result<Unit> =
        runCatching {
            db.from("room_subtasks").insert(
                NewRoomSubtaskDto(taskId = taskId, title = title)
            )
            Unit
        }

    override suspend fun deleteRoomTask(taskId: Long): Result<Unit> =
        runCatching {
            db.from("room_tasks").delete {
                filter { eq("id", taskId) }
            }
            Unit
        }

    override suspend fun toggleTaskCompletion(taskId: Long, currentlyDone: Boolean): Result<Unit> =
        runCatching {
            val me = myId()
            if (currentlyDone) {
                db.from("room_completions").delete {
                    filter {
                        eq("user_id", me)
                        eq("task_id", taskId)
                    }
                }
            } else {
                db.from("room_completions").insert(
                    NewCompletionDto(userId = me, taskId = taskId, subtaskId = null)
                )
            }
            Unit
        }

    override suspend fun toggleSubtaskCompletion(subtaskId: Long, currentlyDone: Boolean): Result<Unit> =
        runCatching {
            val me = myId()
            if (currentlyDone) {
                db.from("room_completions").delete {
                    filter {
                        eq("user_id", me)
                        eq("subtask_id", subtaskId)
                    }
                }
            } else {
                db.from("room_completions").insert(
                    NewCompletionDto(userId = me, taskId = null, subtaskId = subtaskId)
                )
            }
            Unit
        }
}