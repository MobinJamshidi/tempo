package com.mobinjam.tempo.feature.settings.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSettingsDto(
    @SerialName("daily_goal_minutes") val dailyGoalMinutes: Int = 120,
    @SerialName("freezes_used_this_week") val freezesUsedThisWeek: Int = 0,
    @SerialName("freeze_week_start") val freezeWeekStart: String? = null,
)