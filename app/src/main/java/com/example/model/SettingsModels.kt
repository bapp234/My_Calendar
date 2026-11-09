package com.example.model

data class CategorySetting(
    val categoryId: String,
    val enabled: Boolean = true,
    val reminderMinutes: Int = -1 // -1 means use app default
)

data class AppSettings(
    val globalReminderMinutes: Int = 10,
    val ongoingNotificationEnabled: Boolean = true,
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
