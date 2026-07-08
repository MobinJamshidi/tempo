package com.mobinjam.tempo.feature.settings.domain

interface SettingsRepository {

    suspend fun getSettings(): Result<UserSettings>

    suspend fun setDailyGoal(minutes: Int): Result<Unit>
}