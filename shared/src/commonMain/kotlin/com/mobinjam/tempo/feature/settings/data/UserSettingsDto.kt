package com.mobinjam.tempo.feature.settings.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsDto(
    @SerialName("daily_goal_minutes") val dailyGoalMinutes: Int = 120,
)