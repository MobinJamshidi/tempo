package com.mobinjam.tempo.feature.study.domain

data class StudySession(
    val id: Long = 0,
    val durationSeconds: Long,
    val date: String,
    val category: String? = null,
    val startedAt: String? = null,
)