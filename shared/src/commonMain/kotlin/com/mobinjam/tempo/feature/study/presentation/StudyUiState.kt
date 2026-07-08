package com.mobinjam.tempo.feature.study.presentation

import com.mobinjam.tempo.feature.study.domain.StudyStats

enum class TimerStatus { IDLE, RUNNING, PAUSED }

data class StudyUiState(
    val elapsedSeconds: Long = 0,
    val status: TimerStatus = TimerStatus.IDLE,
    val selectedCategory: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val stats: StudyStats = StudyStats(),
    val dailyTotals: Map<String, Long> = emptyMap(),
    val dailyBreakdown: Map<String, List<com.mobinjam.tempo.feature.study.domain.CategoryTime>> = emptyMap(),
    val selectedHeatmapDate: String? = null,
    val dailyGoalMinutes: Int = 120,
    val goalReached: Boolean = false,
    val goalCelebratedToday: Boolean = false,
) {
    val formattedTime: String
        get() {
            val hours = elapsedSeconds / 3600
            val minutes = (elapsedSeconds % 3600) / 60
            val seconds = elapsedSeconds % 60
            return if (hours > 0) {
                twoDigits(hours) + ":" + twoDigits(minutes) + ":" + twoDigits(seconds)
            } else {
                twoDigits(minutes) + ":" + twoDigits(seconds)
            }

        }
    val goalProgress: Float
        get() {
            val goalSeconds = dailyGoalMinutes * 60L
            if (goalSeconds <= 0) return 0f
            val p = stats.todaySeconds.toFloat() / goalSeconds
            return if (p > 1f) 1f else p
        }
}

private fun twoDigits(value: Long): String =
    if (value < 10) "0$value" else value.toString()