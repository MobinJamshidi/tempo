package com.mobinjam.tempo.feature.tasks.domain

enum class TaskPriority(val label: String, val dbValue: String) {
    LOW("Low", "low"),
    MEDIUM("Medium", "medium"),
    HIGH("High", "high");

    companion object {
        fun fromDb(value: String?): TaskPriority =
            entries.firstOrNull { it.dbValue == value } ?: MEDIUM
    }
}