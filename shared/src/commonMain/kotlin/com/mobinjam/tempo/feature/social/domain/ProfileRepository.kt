package com.mobinjam.tempo.feature.social.domain

interface ProfileRepository {

    suspend fun upsertProfile(username: String, displayName: String?): Result<Unit>

    suspend fun getMyProfile(): Result<Profile?>

    suspend fun searchProfiles(query: String): Result<List<Profile>>

    // ensures a profile row exists for the current user, creating one from auth metadata if missing
    suspend fun ensureProfileExists(): Result<Unit>

    // send a friend request to another user
    suspend fun sendFriendRequest(addresseeId: String): Result<Unit>

    // accept a pending friend request
    suspend fun acceptFriendRequest(friendshipId: Long): Result<Unit>

    // remove a friend or cancel/reject a request
    suspend fun removeFriendship(friendshipId: Long): Result<Unit>

    // get all my friends and pending requests
    suspend fun getFriends(): Result<List<FriendProfile>>
}