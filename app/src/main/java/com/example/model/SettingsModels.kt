package com.example.model

enum class OngoingDisplayMode(val label: String) {
    ALWAYS("Luôn hiện"),
    BEFORE_END("Chỉ hiện trước giờ kết thúc"),
    BEFORE_NEXT("Chỉ hiện khi sắp đến việc tiếp theo")
}

enum class OngoingUpdateFrequency(val minutes: Int, val label: String) {
    EVERY_1_MIN(1, "1 phút"),
    EVERY_5_MIN(5, "5 phút"),
    EVERY_15_MIN(15, "15 phút"),
    ON_CHANGE_ONLY(0, "Chỉ khi chuyển việc mới")
}

data class CategorySetting(
    val categoryId: String,
    val enabled: Boolean = true,
    val reminderMinutes: Int = -1 // -1 means use app default
)

data class AppSettings(
    val globalReminderMinutes: Int = 10,
    val ongoingNotificationEnabled: Boolean = false, // Default is false as requested
    val ongoingDisplayMode: OngoingDisplayMode = OngoingDisplayMode.ALWAYS,
    val ongoingBeforeEndMinutes: Int = 15, // 15, 30, 60
    val ongoingBeforeNextMinutes: Int = 15, // 15, 30, 60
    val ongoingUpdateFrequency: OngoingUpdateFrequency = OngoingUpdateFrequency.EVERY_1_MIN,
    val ongoingIncludedCategories: Set<String> = TaskCategory.entries.map { it.name }.toSet(),
    val categorySettings: Map<String, CategorySetting> = emptyMap()
) {
    fun getEffectiveReminderMinutes(categoryId: String): Int {
        val catSetting = categorySettings[categoryId]
        if (catSetting != null && catSetting.reminderMinutes >= 0) {
            return catSetting.reminderMinutes
        }
        return globalReminderMinutes
    }

    fun isCategoryNotificationEnabled(categoryId: String): Boolean {
        return categorySettings[categoryId]?.enabled ?: true
    }

    fun isCategoryIncludedInOngoing(categoryId: String): Boolean {
        return ongoingIncludedCategories.contains(categoryId)
    }
}

object ReminderOptions {
    val OPTIONS = listOf(0, 5, 10, 15, 30, 60)

    fun getLabel(minutes: Int): String {
        return when (minutes) {
            0 -> "Đúng giờ (không nhắc trước)"
            5 -> "Trước 5 phút"
            10 -> "Trước 10 phút"
            15 -> "Trước 15 phút"
            30 -> "Trước 30 phút"
            60 -> "Trước 60 phút (1 giờ)"
            -1 -> "Theo mặc định chung"
            else -> "Trước $minutes phút"
        }
    }
}

object OngoingTimeOptions {
    val BEFORE_END_OPTIONS = listOf(15, 30, 60)
    val BEFORE_NEXT_OPTIONS = listOf(15, 30, 60)
}
