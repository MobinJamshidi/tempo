package com.mobinjam.tempo.feature.tasks.domain

data class Subtask(
    val id: Long = 0,
    val taskId: Long,
    val title: String,
    val isDone: Boolean = false,
)