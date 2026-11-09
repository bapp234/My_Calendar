package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarm.AlarmScheduler
import com.example.data.PreferencesRepository
import com.example.data.ScheduleRepository
import com.example.model.AppSettings
import com.example.model.ScheduleItem
import com.example.notification.NotificationHelper
import com.example.util.ImageStorageManager
import com.example.widget.RoutineGlanceWidgetReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

data class CurrentTaskState(
    val currentTask: ScheduleItem? = null,
    val nextTask: ScheduleItem? = null,
    val remainingSeconds: Long = 0,
    val remainingFormatted: String = "",
    val progressPercent: Float = 0f
)

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = PreferencesRepository(application)

    val settings: StateFlow<AppSettings> = prefsRepo.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, prefsRepo.loadSettings())

    val completedTaskIds: StateFlow<Set<String>> = prefsRepo.completedTasksFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, prefsRepo.getCompletedTaskIdsForToday())

    private val _selectedDayOfWeek = MutableStateFlow(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
    val selectedDayOfWeek: StateFlow<Int> = _selectedDayOfWeek.asStateFlow()

    private val _currentTaskState = MutableStateFlow(calculateCurrentTaskState())
    val currentTaskState: StateFlow<CurrentTaskState> = _currentTaskState.asStateFlow()

    init {
        // Ticking loop every 1 second for live countdown
        viewModelScope.launch {
            while (isActive) {
                _currentTaskState.value = calculateCurrentTaskState()
                delay(1000)
            }
        }

        // Initialize alarms and widgets
        viewModelScope.launch {
            AlarmScheduler.rescheduleAllAlarms(getApplication())
            RoutineGlanceWidgetReceiver.updateAllWidgets(getApplication())
            if (prefsRepo.loadSettings().ongoingNotificationEnabled) {
                triggerOngoingNotificationNow()
            }
        }
    }

    private fun calculateCurrentTaskState(): CurrentTaskState {
        val now = Calendar.getInstance()
        val (current, next) = ScheduleRepository.getCurrentAndNextTask(now)

        if (current == null) {
            val remainingSecsToNext = if (next != null) {
                val nextStartCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, next.startHour)
                    set(Calendar.MINUTE, next.startMinute)
                    set(Calendar.SECOND, 0)
                }
                if (nextStartCalendar.timeInMillis < now.timeInMillis) {
                    nextStartCalendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                ((nextStartCalendar.timeInMillis - now.timeInMillis) / 1000).coerceAtLeast(0)
            } else {
                0L
            }

            val formatted = formatRemaining(remainingSecsToNext)
            return CurrentTaskState(
                currentTask = null,
                nextTask = next,
                remainingSeconds = remainingSecsToNext,
                remainingFormatted = formatted,
                progressPercent = 0f
            )
        }

        val nowSecs = now.get(Calendar.HOUR_OF_DAY) * 3600 + now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND)
        val startSecs = current.startMinutesOfDay * 60
        val endSecs = current.endMinutesOfDay * 60

        val totalDurationSecs = (endSecs - startSecs).coerceAtLeast(1)
        val elapsedSecs = (nowSecs - startSecs).coerceIn(0, totalDurationSecs)
        val remainingSecs = (totalDurationSecs - elapsedSecs).toLong()

        val progress = elapsedSecs.toFloat() / totalDurationSecs.toFloat()
        val formatted = formatRemaining(remainingSecs)

        return CurrentTaskState(
            currentTask = current,
            nextTask = next,
            remainingSeconds = remainingSecs,
            remainingFormatted = formatted,
            progressPercent = progress
        )
    }

    private fun formatRemaining(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }

    fun setSelectedDayOfWeek(dayOfWeek: Int) {
        _selectedDayOfWeek.value = dayOfWeek
    }

    fun toggleTaskCompletion(taskId: String) {
        prefsRepo.toggleTaskCompletedToday(taskId)
        RoutineGlanceWidgetReceiver.updateAllWidgets(getApplication())
    }

    fun resetTodayTasks() {
        prefsRepo.resetTodayTasks()
        RoutineGlanceWidgetReceiver.updateAllWidgets(getApplication())
    }

    fun updateGlobalReminderMinutes(minutes: Int) {
        prefsRepo.updateGlobalReminderMinutes(minutes)
        AlarmScheduler.rescheduleAllAlarms(getApplication())
    }

    fun updateOngoingNotificationEnabled(enabled: Boolean) {
        prefsRepo.updateOngoingNotificationEnabled(enabled)
        if (!enabled) {
            NotificationHelper.cancelOngoingNotification(getApplication())
        } else {
            triggerOngoingNotificationNow()
        }
    }

    fun triggerOngoingNotificationNow() {
        val (current, next) = ScheduleRepository.getCurrentAndNextTask(Calendar.getInstance())
        val remText = _currentTaskState.value.remainingFormatted
        NotificationHelper.updateOngoingNotification(getApplication(), current, next, remText)
    }

    fun updateCategorySetting(categoryId: String, enabled: Boolean, reminderMinutes: Int) {
        prefsRepo.updateCategorySetting(categoryId, enabled, reminderMinutes)
        AlarmScheduler.rescheduleAllAlarms(getApplication())
    }

    fun saveCategoryPhoto(categoryId: String, uri: Uri) {
        viewModelScope.launch {
            val success = ImageStorageManager.saveCategoryPhoto(getApplication(), categoryId, uri)
            if (success) {
                RoutineGlanceWidgetReceiver.updateAllWidgets(getApplication())
            }
        }
    }

    fun deleteCategoryPhoto(categoryId: String) {
        viewModelScope.launch {
            ImageStorageManager.deleteCategoryPhoto(getApplication(), categoryId)
            RoutineGlanceWidgetReceiver.updateAllWidgets(getApplication())
        }
    }

    fun sendTestNotification() {
        NotificationHelper.showTestNotification(getApplication())
    }
}
