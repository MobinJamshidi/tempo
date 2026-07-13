package com.mobinjam.tempo.feature.study.domain

interface StudyRepository {

    suspend fun getSessions(): Result<List<StudySession>>

    suspend fun addSession(
        durationSeconds: Long,
        date: String,
        category: String?,
        startedAt: String?,
    ): Result<Unit>

    suspend fun getStats(): Result<StudyStats>

    suspend fun getDailyTotals(): Result<Map<String, Long>>

    suspend fun getDailyBreakdown(): Result<Map<String, List<CategoryTime>>>

    suspend fun getBestHour(): Result<BestHour?>

    suspend fun getFocusScore(): Result<Int>
}

data class CategoryTime(
    val category: String,
    val seconds: Long,

)
data class BestHour(
    val hour: Int,
    val totalSeconds: Long,


)