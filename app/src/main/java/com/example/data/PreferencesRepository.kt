package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppSettings
import com.example.model.CategorySetting
import com.example.model.TaskCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PreferencesRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("lich_cua_toi_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<AppSettings> = _settingsFlow.asStateFlow()

    private val _completedTasksFlow = MutableStateFlow(getCompletedTaskIdsForToday())
    val completedTasksFlow: StateFlow<Set<String>> = _completedTasksFlow.asStateFlow()

    private fun getTodayDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCompletedTaskIdsForToday(): Set<String> {
        val dateKey = getTodayDateKey()
        return prefs.getStringSet("completed_tasks_$dateKey", emptySet()) ?: emptySet()
    }

    fun isTaskCompletedToday(taskId: String): Boolean {
        return getCompletedTaskIdsForToday().contains(taskId)
    }

    fun setTaskCompletedToday(taskId: String, completed: Boolean) {
        val dateKey = getTodayDateKey()
        val currentSet = getCompletedTaskIdsForToday().toMutableSet()
        if (completed) {
            currentSet.add(taskId)
        } else {
            currentSet.remove(taskId)
        }
        prefs.edit().putStringSet("completed_tasks_$dateKey", currentSet).apply()
        _completedTasksFlow.value = currentSet
    }

    fun toggleTaskCompletedToday(taskId: String) {
        val isCurrently = isTaskCompletedToday(taskId)
        setTaskCompletedToday(taskId, !isCurrently)
    }

    fun resetTodayTasks() {
        val dateKey = getTodayDateKey()
        prefs.edit().remove("completed_tasks_$dateKey").apply()
        _completedTasksFlow.value = emptySet()
    }

    fun loadSettings(): AppSettings {
        val globalReminder = prefs.getInt("global_reminder_minutes", 10)
        val ongoingNotif = prefs.getBoolean("ongoing_notification_enabled", true)

        val catMap = mutableMapOf<String, CategorySetting>()
        for (category in TaskCategory.entries) {
            val enabled = prefs.getBoolean("cat_enabled_${category.name}", true)
            val reminder = prefs.getInt("cat_reminder_${category.name}", -1)
            catMap[category.name] = CategorySetting(
                categoryId = category.name,
                enabled = enabled,
                reminderMinutes = reminder
            )
        }

        return AppSettings(
            globalReminderMinutes = globalReminder,
            ongoingNotificationEnabled = ongoingNotif,
            categorySettings = catMap
        )
    }

    fun updateGlobalReminderMinutes(minutes: Int) {
        prefs.edit().putInt("global_reminder_minutes", minutes).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateOngoingNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("ongoing_notification_enabled", enabled).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateCategorySetting(categoryId: String, enabled: Boolean, reminderMinutes: Int) {
        prefs.edit()
            .putBoolean("cat_enabled_$categoryId", enabled)
            .putInt("cat_reminder_$categoryId", reminderMinutes)
            .apply()
        _settingsFlow.value = loadSettings()
    }

    fun refreshCompletedTasks() {
        _completedTasksFlow.value = getCompletedTaskIdsForToday()
    }
}
