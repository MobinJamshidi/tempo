package com.mobinjam.tempo.feature.settings.domain

interface SettingsRepository {

    suspend fun getSettings(): Result<UserSettings>

    suspend fun setDailyGoal(minutes: Int): Result<Unit>

    suspend fun updateFreezeUsage(weekStart: String, freezesUsed: Int): Result<Unit>

}