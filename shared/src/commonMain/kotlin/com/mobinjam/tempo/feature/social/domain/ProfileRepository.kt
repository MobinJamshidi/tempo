package com.mobinjam.tempo.feature.social.domain

interface ProfileRepository {

    suspend fun upsertProfile(username: String, displayName: String?): Result<Unit>

    suspend fun getMyProfile(): Result<Profile?>

    suspend fun searchProfiles(query: String): Result<List<Profile>>

    suspend fun ensureProfileExists(): Result<Unit>

    suspend fun sendFriendRequest(addresseeId: String): Result<Unit>

    suspend fun acceptFriendRequest(friendshipId: Long): Result<Unit>

    suspend fun removeFriendship(friendshipId: Long): Result<Unit>

    suspend fun getFriends(): Result<List<FriendProfile>>

    suspend fun startActiveSession(category: String?): Result<Unit>

    suspend fun endActiveSession(): Result<Unit>

    suspend fun getActiveFriends(): Result<List<ActiveFriend>>
}

data class ActiveFriend(
    val profile: Profile,
    val category: String?,
    val startedAt: String,
)