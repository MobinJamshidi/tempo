package com.mobinjam.tempo.feature.badges.domain

import com.mobinjam.tempo.core.util.DateUtils
import com.mobinjam.tempo.feature.study.domain.StudySession

object BadgeEvaluator {

    // returns the set of unlocked badge ids based on all study data
    fun evaluate(
        sessions: List<StudySession>,
        currentStreak: Int,
        dailyGoalMinutes: Int,
    ): Set<String> {
        if (sessions.isEmpty()) return emptySet()

        val unlocked = mutableSetOf<String>()

        val totalSeconds = sessions.sumOf { it.durationSeconds }
        val totalHours = totalSeconds / 3600.0
        val sessionCount = sessions.size

        // start
        if (sessionCount >= 1) unlocked.add("first_steps")
        if (sessionCount >= 5) unlocked.add("getting_started")

        // total hours
        if (totalHours >= 1) unlocked.add("rookie")
        if (totalHours >= 10) unlocked.add("focused")
        if (totalHours >= 50) unlocked.add("dedicated")
        if (totalHours >= 100) unlocked.add("scholar")
        if (totalHours >= 250) unlocked.add("master")
        if (totalHours >= 500) unlocked.add("legend")

        // streak
        if (currentStreak >= 3) unlocked.add("on_fire")
        if (currentStreak >= 7) unlocked.add("unstoppable")
        if (currentStreak >= 14) unlocked.add("committed")
        if (currentStreak >= 30) unlocked.add("iron_will")
        if (currentStreak >= 100) unlocked.add("centurion")

        // time of day (based on started_at hour)
        val hours = sessions.mapNotNull { DateUtils.hourOf(it.startedAt) }
        if (hours.any { it in 5..6 }) unlocked.add("early_bird")
        if (hours.any { it in 0..4 }) unlocked.add("night_owl")
        if (hours.any { it in 11..13 }) unlocked.add("lunch_break")

        // goal
        val perDay = sessions.groupBy { it.date }
            .mapValues { (_, list) -> list.sumOf { it.durationSeconds } }
        val goalSeconds = dailyGoalMinutes * 60L
        if (goalSeconds > 0) {
            if (perDay.values.any { it >= goalSeconds }) unlocked.add("goal_getter")
            if (perDay.values.any { it >= goalSeconds * 2 }) unlocked.add("overachiever")
        }

        // daily
        if (perDay.values.any { it >= 3 * 3600 }) unlocked.add("marathon")
        if (sessions.any { it.durationSeconds >= 2 * 3600 }) unlocked.add("deep_focus")

        // variety
        val distinctCategories = sessions.mapNotNull { it.category }.distinct().size
        if (distinctCategories >= 5) unlocked.add("explorer")
        if (distinctCategories >= 10) unlocked.add("well_rounded")

        return unlocked
    }
}