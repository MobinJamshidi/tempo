package com.mobinjam.tempo.feature.tasks.domain

data class Task(
    val id: Long = 0,
    val title: String,
    val isDone: Boolean = false,
    val dueDate: String? = null, // format: "2026-07-05"
)