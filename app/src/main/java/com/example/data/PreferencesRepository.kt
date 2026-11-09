package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppSettings
import com.example.model.CategorySetting
import com.example.model.OngoingDisplayMode
import com.example.model.OngoingUpdateFrequency
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
        val ongoingNotif = prefs.getBoolean("ongoing_notification_enabled", false) // Default is false

        val displayModeStr = prefs.getString("ongoing_display_mode", OngoingDisplayMode.ALWAYS.name) ?: OngoingDisplayMode.ALWAYS.name
        val displayMode = try { OngoingDisplayMode.valueOf(displayModeStr) } catch (e: Exception) { OngoingDisplayMode.ALWAYS }

        val beforeEndMinutes = prefs.getInt("ongoing_before_end_minutes", 15)
        val beforeNextMinutes = prefs.getInt("ongoing_before_next_minutes", 15)

        val freqStr = prefs.getString("ongoing_update_frequency", OngoingUpdateFrequency.EVERY_1_MIN.name) ?: OngoingUpdateFrequency.EVERY_1_MIN.name
        val updateFreq = try { OngoingUpdateFrequency.valueOf(freqStr) } catch (e: Exception) { OngoingUpdateFrequency.EVERY_1_MIN }

        val defaultCategories = TaskCategory.entries.map { it.name }.toSet()
        val includedCategories = prefs.getStringSet("ongoing_included_categories", null) ?: defaultCategories

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
            ongoingDisplayMode = displayMode,
            ongoingBeforeEndMinutes = beforeEndMinutes,
            ongoingBeforeNextMinutes = beforeNextMinutes,
            ongoingUpdateFrequency = updateFreq,
            ongoingIncludedCategories = includedCategories,
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

    fun updateOngoingDisplayMode(mode: OngoingDisplayMode) {
        prefs.edit().putString("ongoing_display_mode", mode.name).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateOngoingBeforeEndMinutes(minutes: Int) {
        prefs.edit().putInt("ongoing_before_end_minutes", minutes).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateOngoingBeforeNextMinutes(minutes: Int) {
        prefs.edit().putInt("ongoing_before_next_minutes", minutes).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateOngoingUpdateFrequency(frequency: OngoingUpdateFrequency) {
        prefs.edit().putString("ongoing_update_frequency", frequency.name).apply()
        _settingsFlow.value = loadSettings()
    }

    fun updateOngoingIncludedCategories(categories: Set<String>) {
        prefs.edit().putStringSet("ongoing_included_categories", categories).apply()
        _settingsFlow.value = loadSettings()
    }

    fun toggleOngoingCategory(categoryId: String) {
        val currentSet = loadSettings().ongoingIncludedCategories.toMutableSet()
        if (currentSet.contains(categoryId)) {
            currentSet.remove(categoryId)
        } else {
            currentSet.add(categoryId)
        }
        updateOngoingIncludedCategories(currentSet)
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
