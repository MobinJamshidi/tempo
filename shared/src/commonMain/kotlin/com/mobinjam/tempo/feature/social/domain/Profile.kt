package com.mobinjam.tempo.feature.social.domain

data class Profile(
    val id: String,
    val username: String,
    val displayName: String?,
)

enum class FriendStatus {
    NONE,        // no relationship
    PENDING_SENT,    // I sent a request, waiting
    PENDING_RECEIVED, // they sent me a request
    FRIENDS,     // we are friends
}

data class FriendProfile(
    val profile: Profile,
    val status: FriendStatus,
    val friendshipId: Long? = null,
)