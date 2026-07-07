package com.mobinjam.tempo.feature.study.data

import com.mobinjam.tempo.core.data.remote.SupabaseClientProvider
import com.mobinjam.tempo.feature.study.domain.StudySession
import com.mobinjam.tempo.feature.study.domain.StudyRepository
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

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
}