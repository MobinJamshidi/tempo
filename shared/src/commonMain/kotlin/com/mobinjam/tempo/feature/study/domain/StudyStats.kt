package com.mobinjam.tempo.feature.study.domain

data class StudyStats(
    val todaySeconds: Long = 0,
    val weekSeconds: Long = 0,
    val streakDays: Int = 0,
    val lastWeekSeconds: Long = 0,
)