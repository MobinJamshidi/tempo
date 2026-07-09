package com.mobinjam.tempo.feature.settings.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.feature.settings.domain.SettingsRepository
import com.mobinjam.tempo.feature.settings.domain.UserSettings
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest

class SupabaseSettingsRepository : SettingsRepository {

    private val db = SupabaseClientProvider.client.postgrest

    override suspend fun getSettings(): Result<UserSettings> =
        runCatching {
            val rows = db.from("user_settings")
                .select()
                .decodeList<UserSettingsDto>()

            val dto = rows.firstOrNull() ?: UserSettingsDto()
            UserSettings(
                dailyGoalMinutes = dto.dailyGoalMinutes,
                freezesUsedThisWeek = dto.freezesUsedThisWeek,
                freezeWeekStart = dto.freezeWeekStart,
            )
        }

    override suspend fun setDailyGoal(minutes: Int): Result<Unit> =
        runCatching {
            val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            // upsert: insert or update the user's settings row
            db.from("user_settings").upsert(
                buildMap {
                    put("user_id", userId)
                    put("daily_goal_minutes", minutes)
                }
            )
            Unit
        }
    override suspend fun updateFreezeUsage(weekStart: String, freezesUsed: Int): Result<Unit> =
        runCatching {
            val userId = SupabaseClientProvider.client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Not logged in")

            db.from("user_settings").upsert(
                buildMap {
                    put("user_id", userId)
                    put("freeze_week_start", weekStart)
                    put("freezes_used_this_week", freezesUsed)
                }
            )
            Unit
        }

}