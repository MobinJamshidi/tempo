package com.mobinjam.tempo.feature.social.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveSessionDto(
    @SerialName("user_id") val userId: String,
    @SerialName("started_at") val startedAt: String,
    val category: String? = null,
)