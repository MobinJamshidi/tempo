package com.mobinjam.tempo.feature.social.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.feature.social.domain.Profile
import com.mobinjam.tempo.feature.social.domain.ProfileRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.TextSearchType

class SupabaseProfileRepository : ProfileRepository {

    private val db = SupabaseClientProvider.client.postgrest

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

            val rows = db.from("profiles")
                .select {
                    filter { eq("id", userId) }
                }
                .decodeList<ProfileDto>()

            rows.firstOrNull()?.let {
                Profile(id = it.id, username = it.username, displayName = it.displayName)
            }
        }

    override suspend fun searchProfiles(query: String): Result<List<Profile>> =
        runCatching {
            if (query.isBlank()) return@runCatching emptyList()

            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id

            val rows = db.from("profiles")
                .select {
                    filter { ilike("username", "%$query%") }
                    limit(20)
                }
                .decodeList<ProfileDto>()

            rows
                .filter { it.id != myId } // don't show myself in search
                .map { Profile(id = it.id, username = it.username, displayName = it.displayName) }
        }
    override suspend fun ensureProfileExists(): Result<Unit> =
        runCatching {
            val user = SupabaseClientProvider.client.auth.currentUserOrNull()
                ?: throw IllegalStateException("Not logged in")

            // check if profile already exists
            val existing = db.from("profiles")
                .select {
                    filter { eq("id", user.id) }
                }
                .decodeList<ProfileDto>()

            if (existing.isNotEmpty()) return@runCatching Unit

            // read username from auth metadata
            val username = user.userMetadata?.get("username")
                ?.toString()
                ?.trim('"')
                ?: "user_${user.id.take(6)}"

            db.from("profiles").insert(
                buildMap {
                    put("id", user.id)
                    put("username", username)
                }
            )
            Unit
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

    override suspend fun getFriends(): Result<List<com.mobinjam.tempo.feature.social.domain.FriendProfile>> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            // get all friendships involving me
            val friendships = db.from("friendships")
                .select()
                .decodeList<FriendshipDto>()
                .filter { it.requesterId == myId || it.addresseeId == myId }

            if (friendships.isEmpty()) return@runCatching emptyList()

            // collect the other user ids
            val otherIds = friendships.map {
                if (it.requesterId == myId) it.addresseeId else it.requesterId
            }.distinct()

            // fetch their profiles
            val profiles = db.from("profiles")
                .select {
                    filter { isIn("id", otherIds) }
                }
                .decodeList<ProfileDto>()
                .associateBy { it.id }

            // build FriendProfile list with status
            friendships.mapNotNull { f ->
                val otherId = if (f.requesterId == myId) f.addresseeId else f.requesterId
                val profileDto = profiles[otherId] ?: return@mapNotNull null

                val status = when {
                    f.status == "accepted" -> com.mobinjam.tempo.feature.social.domain.FriendStatus.FRIENDS
                    f.requesterId == myId -> com.mobinjam.tempo.feature.social.domain.FriendStatus.PENDING_SENT
                    else -> com.mobinjam.tempo.feature.social.domain.FriendStatus.PENDING_RECEIVED
                }

                com.mobinjam.tempo.feature.social.domain.FriendProfile(
                    profile = com.mobinjam.tempo.feature.social.domain.Profile(
                        id = profileDto.id,
                        username = profileDto.username,
                        displayName = profileDto.displayName,
                    ),
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

    override suspend fun getActiveFriends(): Result<List<com.mobinjam.tempo.feature.social.domain.ActiveFriend>> =
        runCatching {
            val myId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            // get my accepted friendships
            val friendships = db.from("friendships")
                .select()
                .decodeList<FriendshipDto>()
                .filter { (it.requesterId == myId || it.addresseeId == myId) && it.status == "accepted" }

            val friendIds = friendships.map {
                if (it.requesterId == myId) it.addresseeId else it.requesterId
            }.distinct()

            if (friendIds.isEmpty()) return@runCatching emptyList()

            // get active sessions of those friends
            val activeSessions = db.from("active_sessions")
                .select()
                .decodeList<ActiveSessionDto>()
                .filter { it.userId in friendIds }

            if (activeSessions.isEmpty()) return@runCatching emptyList()

            // fetch their profiles
            val profiles = db.from("profiles")
                .select {
                    filter { isIn("id", activeSessions.map { s -> s.userId }) }
                }
                .decodeList<ProfileDto>()
                .associateBy { it.id }

            activeSessions.mapNotNull { session ->
                val p = profiles[session.userId] ?: return@mapNotNull null
                com.mobinjam.tempo.feature.social.domain.ActiveFriend(
                    profile = com.mobinjam.tempo.feature.social.domain.Profile(
                        id = p.id,
                        username = p.username,
                        displayName = p.displayName,
                    ),
                    category = session.category,
                    startedAt = session.startedAt,
                )
            }
        }

}