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
}

private fun twoDigits(value: Long): String =
    if (value < 10) "0$value" else value.toString()