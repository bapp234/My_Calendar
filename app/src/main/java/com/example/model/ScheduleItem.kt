package com.example.model

import java.util.Locale

data class ScheduleItem(
    val id: String,
    val dayOfWeek: Int, // Calendar.MONDAY to Calendar.SUNDAY
    val dayName: String, // "Thứ 2", "Thứ 3", etc.
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val title: String,
    val description: String = "",
    val category: TaskCategory,
    val crossMidnightNote: String? = null
) {
    val startTimeFormatted: String
        get() = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute)

    val endTimeFormatted: String
        get() = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute)

    val timeRangeFormatted: String
        get() = "$startTimeFormatted - $endTimeFormatted"

    val startMinutesOfDay: Int
        get() = startHour * 60 + startMinute

    val endMinutesOfDay: Int
        get() {
            val endMins = endHour * 60 + endMinute
            return if (endMins <= startMinutesOfDay) endMins + 24 * 60 else endMins
        }

    val durationMinutes: Int
        get() = endMinutesOfDay - startMinutesOfDay
}
