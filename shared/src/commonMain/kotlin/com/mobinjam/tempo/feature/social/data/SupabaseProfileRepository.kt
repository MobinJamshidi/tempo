package com.mobinjam.tempo.feature.social.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.feature.social.domain.ActiveFriend
import com.mobinjam.tempo.feature.social.domain.FriendProfile
import com.mobinjam.tempo.feature.social.domain.FriendStatus
import com.mobinjam.tempo.feature.social.domain.Profile
import com.mobinjam.tempo.feature.social.domain.ProfileRepository
import com.mobinjam.tempo.feature.study.data.StudySessionDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.JsonPrimitive

class SupabaseProfileRepository : ProfileRepository {

    private val db = SupabaseClientProvider.client.postgrest

    private fun toProfile(dto: ProfileDto) = Profile(
        id = dto.id,
        username = dto.username,
        displayName = dto.displayName,
        avatarUrl = dto.avatarUrl,
    )

    override suspend fun upsertProfile(username: String, displayName: String?): Result<Unit> =
        runCatching {
            val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            db.from("profiles").upsert(
                buildMap {
                    put("id", userId)
                    put("username", username)
                    if (displayName != null) put("display_name", displayName)
                }
            )
            Unit
        }

    override suspend fun getMyProfile(): Result<Profile?> =
        runCatching {
            val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: return@runCatching null

            db.from("profiles")
                .select {
                    filter { eq("id", userId) }
                }
                .decodeList<ProfileDto>()
                .firstOrNull()
                ?.let { toProfile(it) }
        }

    override suspend fun searchProfiles(query: String): Result<List<Profile>> =
        runCatching {
            if (query.isBlank()) return@runCatching emptyList()

            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id

            db.from("profiles")
                .select {
                    filter { ilike("username", "%$query%") }
                    limit(20)
                }
                .decodeList<ProfileDto>()
                .filter { it.id != myId }
                .map { toProfile(it) }
        }

    override suspend fun ensureProfileExists(): Result<Unit> =
        runCatching {
            val user = SupabaseClientProvider.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("Not logged in")

            val existing = db.from("profiles")
                .select {
                    filter { eq("id", user.id) }
                }
                .decodeList<ProfileDto>()

            if (existing.isNotEmpty()) return@runCatching Unit

            val username = user.userMetadata
                ?.get("username")
                ?.let { element -> (element as? JsonPrimitive)?.content }
                ?.takeIf { it.isNotBlank() }
                ?: "user_${user.id.take(6)}"

            db.from("profiles").insert(
                buildMap {
                    put("id", user.id)
                    put("username", username)
                }
            )
            Unit
        }

    override suspend fun updateUsername(newUsername: String): Result<Unit> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            db.from("profiles").update(
                buildMap { put("username", newUsername) }
            ) {
                filter { eq("id", myId) }
            }
            Unit
        }

    override suspend fun updatePassword(newPassword: String): Result<Unit> =
        runCatching {
            SupabaseClientProvider.client.auth.updateUser {
                password = newPassword
            }
            Unit
        }

    override suspend fun uploadAvatar(bytes: ByteArray): Result<String> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            val bucket = SupabaseClientProvider.client.storage.from("avatars")
            val path = "$myId.jpg"

            bucket.upload(path, bytes) {
                upsert = true
            }

            // add a cache-busting suffix so the new image shows immediately
            val publicUrl = bucket.publicUrl(path) + "?v=" + kotlin.time.Clock.System.now().toEpochMilliseconds()

            db.from("profiles").update(
                buildMap { put("avatar_url", publicUrl) }
            ) {
                filter { eq("id", myId) }
            }

            publicUrl
        }

    override suspend fun getFriendCount(): Result<Int> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: return@runCatching 0

            db.from("friendships")
                .select()
                .decodeList<FriendshipDto>()
                .count { (it.requesterId == myId || it.addresseeId == myId) && it.status == "accepted" }
        }

    override suspend fun sendFriendRequest(addresseeId: String): Result<Unit> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            db.from("friendships").insert(
                buildMap {
                    put("requester_id", myId)
                    put("addressee_id", addresseeId)
                    put("status", "pending")
                }
            )
            Unit
        }

    override suspend fun acceptFriendRequest(friendshipId: Long): Result<Unit> =
        runCatching {
            db.from("friendships").update(
                buildMap { put("status", "accepted") }
            ) {
                filter { eq("id", friendshipId) }
            }
            Unit
        }

    override suspend fun removeFriendship(friendshipId: Long): Result<Unit> =
        runCatching {
            db.from("friendships").delete {
                filter { eq("id", friendshipId) }
            }
            Unit
        }

    override suspend fun getFriends(): Result<List<FriendProfile>> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            val friendships = db.from("friendships")
                .select()
                .decodeList<FriendshipDto>()
                .filter { it.requesterId == myId || it.addresseeId == myId }

            if (friendships.isEmpty()) return@runCatching emptyList()

            val otherIds = friendships.map {
                if (it.requesterId == myId) it.addresseeId else it.requesterId
            }.distinct()

            val profiles = db.from("profiles")
                .select {
                    filter { isIn("id", otherIds) }
                }
                .decodeList<ProfileDto>()
                .associateBy { it.id }

            friendships.mapNotNull { f ->
                val otherId = if (f.requesterId == myId) f.addresseeId else f.requesterId
                val dto = profiles[otherId] ?: return@mapNotNull null

                val status = when {
                    f.status == "accepted" -> FriendStatus.FRIENDS
                    f.requesterId == myId -> FriendStatus.PENDING_SENT
                    else -> FriendStatus.PENDING_RECEIVED
                }

                FriendProfile(
                    profile = toProfile(dto),
                    status = status,
                    friendshipId = f.id,
                )
            }
        }

    override suspend fun startActiveSession(category: String?): Result<Unit> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            db.from("active_sessions").upsert(
                buildMap {
                    put("user_id", myId)
                    put("started_at", com.mobinjam.tempo.core.util.DateUtils.nowTimestamp())
                    if (category != null) put("category", category)
                }
            )
            Unit
        }

    override suspend fun endActiveSession(): Result<Unit> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            db.from("active_sessions").delete {
                filter { eq("user_id", myId) }
            }
            Unit
        }

    override suspend fun getActiveFriends(): Result<List<ActiveFriend>> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            val friendships = db.from("friendships")
                .select()
                .decodeList<FriendshipDto>()
                .filter { (it.requesterId == myId || it.addresseeId == myId) && it.status == "accepted" }

            val friendIds = friendships.map {
                if (it.requesterId == myId) it.addresseeId else it.requesterId
            }.distinct()

            if (friendIds.isEmpty()) return@runCatching emptyList()

            val activeSessions = db.from("active_sessions")
                .select()
                .decodeList<ActiveSessionDto>()
                .filter { it.userId in friendIds }

            if (activeSessions.isEmpty()) return@runCatching emptyList()

            val profiles = db.from("profiles")
                .select {
                    filter { isIn("id", activeSessions.map { s -> s.userId }) }
                }
                .decodeList<ProfileDto>()
                .associateBy { it.id }

            activeSessions.mapNotNull { session ->
                val dto = profiles[session.userId] ?: return@mapNotNull null
                ActiveFriend(
                    profile = toProfile(dto),
                    category = session.category,
                    startedAt = session.startedAt,
                )
            }
        }

    override suspend fun getGlobalActive(): Result<List<ActiveFriend>> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id

            val activeSessions = db.from("active_sessions")
                .select()
                .decodeList<ActiveSessionDto>()

            if (activeSessions.isEmpty()) return@runCatching emptyList()

            val activeUserIds = activeSessions.map { it.userId }

            val profiles = db.from("profiles")
                .select {
                    filter { isIn("id", activeUserIds) }
                }
                .decodeList<ProfileDto>()
                .associateBy { it.id }

            val today = com.mobinjam.tempo.core.util.DateUtils.toDbString(
                com.mobinjam.tempo.core.util.DateUtils.today()
            )
            val todaySessions = db.from("study_sessions")
                .select {
                    filter {
                        isIn("user_id", activeUserIds)
                        eq("date", today)
                    }
                }
                .decodeList<StudySessionDto>()

            val todayTotals = todaySessions
                .groupBy { it.userId }
                .mapValues { (_, list) -> list.sumOf { it.durationSeconds } }

            activeSessions.mapNotNull { session ->
                if (session.userId == myId) return@mapNotNull null
                val dto = profiles[session.userId] ?: return@mapNotNull null
                ActiveFriend(
                    profile = toProfile(dto),
                    category = session.category,
                    startedAt = session.startedAt,
                    todaySecondsBefore = todayTotals[session.userId] ?: 0L,
                )
            }
        }
}