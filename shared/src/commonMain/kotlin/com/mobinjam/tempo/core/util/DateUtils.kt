package com.mobinjam.tempo.core.util

import kotlin.time.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import kotlinx.datetime.DateTimeUnit

object DateUtils {

    // today's date in the device's timezone
    fun today(): LocalDate =
        Clock.System.todayIn(TimeZone.currentSystemDefault())

    // convert a LocalDate to a database string like "2026-07-05"
    fun toDbString(date: LocalDate): String = date.toString()

    // parse a database string back to LocalDate (or null if invalid)
    fun fromDbString(value: String?): LocalDate? =
        if (value.isNullOrBlank()) null
        else try { LocalDate.parse(value) } catch (e: Exception) { null }

    // returns the 7 dates of the week that contains [date], starting Monday
    fun weekDaysOf(date: LocalDate): List<LocalDate> {
        // how many days since Monday (Monday=0 ... Sunday=6)
        val daysFromMonday = date.dayOfWeek.ordinal
        val monday = date.minus(daysFromMonday, DateTimeUnit.DAY)
        return (0..6).map { monday.plus(it, DateTimeUnit.DAY) }
    }

    // short weekday name, e.g. "Mon"
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

    // full month name, e.g. "July"
    fun monthName(date: LocalDate): String =
        when (date.monthNumber) {
            1 -> "January"; 2 -> "February"; 3 -> "March"; 4 -> "April"
            5 -> "May"; 6 -> "June"; 7 -> "July"; 8 -> "August"
            9 -> "September"; 10 -> "October"; 11 -> "November"; 12 -> "December"
            else -> ""
        }

    // all days to display in a month grid (including leading blanks as null)
    fun monthGrid(year: Int, month: Int): List<LocalDate?> {
        val firstOfMonth = LocalDate(year, month, 1)
        val daysInMonth = daysInMonth(year, month)

        // leading empty cells so the 1st lands under its weekday (Monday-first)
        val leadingBlanks = firstOfMonth.dayOfWeek.ordinal

        val cells = mutableListOf<LocalDate?>()
        repeat(leadingBlanks) { cells.add(null) }
        for (day in 1..daysInMonth) {
            cells.add(LocalDate(year, month, day))
        }
        return cells
    }

    private fun daysInMonth(year: Int, month: Int): Int {
        // count forward from day 1 until the date becomes invalid
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
}