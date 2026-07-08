package com.mobinjam.tempo.feature.study.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.feature.study.domain.StudySession
import com.mobinjam.tempo.feature.study.domain.StudyRepository
import com.mobinjam.tempo.feature.study.domain.StudyStats
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.datetime.minus

class SupabaseStudyRepository : StudyRepository {

    private val db = SupabaseClientProvider.client.postgrest

    override suspend fun getSessions(): Result<List<StudySession>> =
        runCatching {
            val result = db.from("study_sessions")
                .select {
                    order("date", Order.DESCENDING)
                }
                .decodeList<StudySessionDto>()

            result.map { dto ->
                StudySession(
                    id = dto.id,
                    durationSeconds = dto.durationSeconds,
                    date = dto.date,
                    category = dto.category,
                    startedAt = dto.startedAt,
                )
            }
        }

    override suspend fun addSession(
        durationSeconds: Long,
        date: String,
        category: String?,
        startedAt: String?,
    ): Result<Unit> =
        runCatching {
            db.from("study_sessions").insert(
                StudySessionDto(
                    durationSeconds = durationSeconds,
                    date = date,
                    category = category,
                    startedAt = startedAt,
                )
            )
            Unit
        }
    override suspend fun getStats(): Result<StudyStats> =
        getSessions().map { sessions ->
            val today = com.mobinjam.tempo.core.util.DateUtils.today()
            val todayStr = com.mobinjam.tempo.core.util.DateUtils.toDbString(today)

            // today's total
            val todaySeconds = sessions
                .filter { it.date == todayStr }
                .sumOf { it.durationSeconds }

            // last 7 days total
            val weekDates = com.mobinjam.tempo.core.util.DateUtils.last7Days()
                .map { com.mobinjam.tempo.core.util.DateUtils.toDbString(it) }
                .toSet()
            val weekSeconds = sessions
                .filter { it.date in weekDates }
                .sumOf { it.durationSeconds }
            // the 7 days before this week (days 8..14 ago)
            val lastWeekDates = (7..13).map {
                com.mobinjam.tempo.core.util.DateUtils.toDbString(
                    today.minus(it, kotlinx.datetime.DateTimeUnit.DAY)
                )
            }.toSet()
            val lastWeekSeconds = sessions
                .filter { it.date in lastWeekDates }
                .sumOf { it.durationSeconds }

            // streak: count consecutive days back from today that have study time
            val studiedDates = sessions.map { it.date }.toSet()
            var streak = 0
            var cursor = today
            while (true) {
                val cursorStr = com.mobinjam.tempo.core.util.DateUtils.toDbString(cursor)
                if (cursorStr in studiedDates) {
                    streak++
                    cursor = cursor.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
                } else {
                    break
                }
            }

            StudyStats(
                todaySeconds = todaySeconds,
                weekSeconds = weekSeconds,
                streakDays = streak,
            )
            StudyStats(
                todaySeconds = todaySeconds,
                weekSeconds = weekSeconds,
                streakDays = streak,
                lastWeekSeconds = lastWeekSeconds,
            )
        }
    override suspend fun getDailyTotals(): Result<Map<String, Long>> =
        getSessions().map { sessions ->
            sessions.groupBy { it.date }
                .mapValues { (_, list) -> list.sumOf { it.durationSeconds } }
        }
    override suspend fun getDailyBreakdown(): Result<Map<String, List<com.mobinjam.tempo.feature.study.domain.CategoryTime>>> =
        getSessions().map { sessions ->
            sessions.groupBy { it.date }.mapValues { (_, daySessions) ->
                daySessions
                    .groupBy { it.category ?: "Other" }
                    .map { (cat, list) ->
                        com.mobinjam.tempo.feature.study.domain.CategoryTime(
                            category = cat,
                            seconds = list.sumOf { it.durationSeconds },
                        )
                    }
                    .sortedByDescending { it.seconds }
            }
        }
}