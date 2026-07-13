package com.mobinjam.tempo.feature.settings.domain

data class UserSettings(
    val dailyGoalMinutes: Int = 120,
    val freezesUsedThisWeek: Int = 0,
    val freezeWeekStart: String? = null,
)