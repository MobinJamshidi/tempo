package com.mobinjam.tempo.core.util

import kotlin.time.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toInstant

object DateUtils {

    fun today(): LocalDate =
        Clock.System.todayIn(TimeZone.currentSystemDefault())

    fun toDbString(date: LocalDate): String = date.toString()

    fun fromDbString(value: String?): LocalDate? =
        if (value.isNullOrBlank()) null
        else try { LocalDate.parse(value) } catch (e: Exception) { null }

    fun weekDaysOf(date: LocalDate): List<LocalDate> {
        val daysFromMonday = date.dayOfWeek.ordinal
        val monday = date.minus(daysFromMonday, DateTimeUnit.DAY)
        return (0..6).map { monday.plus(it, DateTimeUnit.DAY) }
    }

    fun weekdayShort(date: LocalDate): String =
        when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "Mon"
            DayOfWeek.TUESDAY -> "Tue"
            DayOfWeek.WEDNESDAY -> "Wed"
            DayOfWeek.THURSDAY -> "Thu"
            DayOfWeek.FRIDAY -> "Fri"
            DayOfWeek.SATURDAY -> "Sat"
            DayOfWeek.SUNDAY -> "Sun"
            else -> ""
        }

    fun monthName(date: LocalDate): String =
        when (date.monthNumber) {
            1 -> "January"; 2 -> "February"; 3 -> "March"; 4 -> "April"
            5 -> "May"; 6 -> "June"; 7 -> "July"; 8 -> "August"
            9 -> "September"; 10 -> "October"; 11 -> "November"; 12 -> "December"
            else -> ""
        }

    fun monthGrid(year: Int, month: Int): List<LocalDate?> {
        val firstOfMonth = LocalDate(year, month, 1)
        val daysInMonth = daysInMonth(year, month)
        val leadingBlanks = firstOfMonth.dayOfWeek.ordinal

        val cells = mutableListOf<LocalDate?>()
        repeat(leadingBlanks) { cells.add(null) }
        for (day in 1..daysInMonth) {
            cells.add(LocalDate(year, month, day))
        }
        return cells
    }

    private fun daysInMonth(year: Int, month: Int): Int {
        var count = 0
        var day = 1
        while (true) {
            try {
                LocalDate(year, month, day)
                count = day
                day++
            } catch (e: Exception) {
                break
            }
        }
        return count
    }

    fun last7Days(): List<LocalDate> {
        val today = today()
        return (0..6).map { today.minus(it, DateTimeUnit.DAY) }
    }

    fun isDayBefore(a: LocalDate, b: LocalDate): Boolean =
        a.plus(1, DateTimeUnit.DAY) == b

    fun nowTimestamp(): String {
        val now = kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return now.toString()
    }

    fun hourOf(timestamp: String?): Int? {
        if (timestamp.isNullOrBlank()) return null
        return try {
            // normalize: replace space with T, drop timezone/fraction
            val normalized = timestamp
                .replace(" ", "T")
                .substringBefore(".")
                .substringBefore("+")
                .trim()
            // extract hour from "2026-07-09T01:07:46" → the part after T, before first :
            val timePart = normalized.substringAfter("T", "")
            timePart.substringBefore(":").toIntOrNull()
        } catch (e: Exception) {
            null
        }
    }

    // seconds elapsed since a given timestamp string
    fun secondsSince(timestamp: String?): Long {
        if (timestamp.isNullOrBlank()) return 0
        return try {
            val normalized = timestamp
                .replace(" ", "T")
                .substringBefore(".")
                .substringBefore("+")
                .trim()
            val started = kotlinx.datetime.LocalDateTime.parse(normalized)
                .toInstant(TimeZone.currentSystemDefault())
            val now = kotlin.time.Clock.System.now()
            (now - started).inWholeSeconds.coerceAtLeast(0)
        } catch (e: Exception) {
            0
        }
    }
}