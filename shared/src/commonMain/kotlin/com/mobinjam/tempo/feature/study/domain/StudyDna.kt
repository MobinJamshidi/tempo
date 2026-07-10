package com.mobinjam.tempo.feature.study.domain

import com.mobinjam.tempo.core.util.DateUtils

data class StudyDnaTrait(
    val label: String,
    val value: String,
    val emoji: String,
    val reason: String,
)

object StudyDnaAnalyzer {

    fun analyze(sessions: List<StudySession>): List<StudyDnaTrait>? {
        if (sessions.isEmpty()) return null

        val traits = mutableListOf<StudyDnaTrait>()

        // 1. favorite time of day
        val hours = sessions.mapNotNull { DateUtils.hourOf(it.startedAt) }
        if (hours.isNotEmpty()) {
            val avgHour = hours.average().toInt()
            val (label, emoji) = when {
                avgHour < 7 -> "Early Bird" to "🌅"
                avgHour < 12 -> "Morning Person" to "☀️"
                avgHour < 17 -> "Afternoon Focus" to "🌤️"
                avgHour < 21 -> "Evening Learner" to "🌇"
                else -> "Night Owl" to "🌙"
            }
            traits.add(
                StudyDnaTrait(
                    label = "Your rhythm",
                    value = label,
                    emoji = emoji,
                    reason = "You usually study around ${formatHour(avgHour)}",
                )
            )
        }

        // 2. session style
        val avgMinutes = (sessions.sumOf { it.durationSeconds }.toDouble() / sessions.size / 60.0).toInt()
        val (styleLabel, styleEmoji) = when {
            avgMinutes >= 60 -> "Deep Diver" to "🌊"
            avgMinutes >= 25 -> "Steady Worker" to "⚙️"
            else -> "Quick Sprinter" to "⚡"
        }
        traits.add(
            StudyDnaTrait(
                label = "Your style",
                value = styleLabel,
                emoji = styleEmoji,
                reason = "Your sessions average $avgMinutes min",
            )
        )

        // 3. favorite subject
        val categoryCounts = sessions.mapNotNull { it.category }.groupingBy { it }.eachCount()
        val topCategory = categoryCounts.maxByOrNull { it.value }
        if (topCategory != null) {
            traits.add(
                StudyDnaTrait(
                    label = "Your focus",
                    value = topCategory.key,
                    emoji = "📚",
                    reason = "${topCategory.value} of your sessions were here",
                )
            )
        }

        // 4. consistency
        val distinctDays = sessions.map { it.date }.distinct().size
        val (consLabel, consEmoji) = when {
            distinctDays >= 14 -> "Very Consistent" to "🔥"
            distinctDays >= 5 -> "Building Habit" to "🌱"
            else -> "Just Starting" to "✨"
        }
        traits.add(
            StudyDnaTrait(
                label = "Your consistency",
                value = consLabel,
                emoji = consEmoji,
                reason = "You've studied on $distinctDays different days",
            )
        )

        return traits
    }

    private fun formatHour(hour: Int): String {
        return when {
            hour == 0 -> "12 AM"
            hour < 12 -> "$hour AM"
            hour == 12 -> "12 PM"
            else -> "${hour - 12} PM"
        }
    }
}