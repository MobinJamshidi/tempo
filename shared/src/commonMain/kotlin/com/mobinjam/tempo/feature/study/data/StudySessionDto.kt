package com.mobinjam.tempo.feature.study.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudySessionDto(
    val id: Long = 0,
    @SerialName("user_id") val userId: String = "",
    @SerialName("duration_seconds") val durationSeconds: Long,
    val date: String,
    val category: String? = null,
    @SerialName("started_at") val startedAt: String? = null,
)