package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.PreferencesRepository
import com.example.data.ScheduleRepository
import com.example.notification.NotificationHelper
import com.example.widget.RoutineGlanceWidgetReceiver
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            AlarmScheduler.ACTION_TASK_ALARM -> {
                val taskId = intent.getStringExtra("EXTRA_TASK_ID") ?: return
                val isSnooze = intent.getBooleanExtra("EXTRA_IS_SNOOZE", false)
                val task = ScheduleRepository.findItemById(taskId)
                if (task != null) {
                    NotificationHelper.showTaskNotification(
                        context = context,
                        task = task,
                        isAdvanceReminder = false
                    )
                }
                updateOngoingAndWidgets(context)
            }

            AlarmScheduler.ACTION_TASK_REMINDER -> {
                val taskId = intent.getStringExtra("EXTRA_TASK_ID") ?: return
                val advanceMinutes = intent.getIntExtra("EXTRA_ADVANCE_MINUTES", 10)
                val task = ScheduleRepository.findItemById(taskId)
                if (task != null) {
                    NotificationHelper.showTaskNotification(
                        context = context,
                        task = task,
                        isAdvanceReminder = true,
                        minutesAdvance = advanceMinutes
                    )
                }
            }

            "com.example.ACTION_TASK_COMPLETE" -> {
                val taskId = intent.getStringExtra("EXTRA_TASK_ID") ?: return
                val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", -1)
                if (notificationId != -1) {
                    NotificationHelper.cancelNotification(context, notificationId)
                }
                if (taskId != "test_task") {
                    val prefsRepo = PreferencesRepository(context)
                    prefsRepo.setTaskCompletedToday(taskId, true)
                }
                updateOngoingAndWidgets(context)
            }

            "com.example.ACTION_TASK_SNOOZE" -> {
                val taskId = intent.getStringExtra("EXTRA_TASK_ID") ?: return
                val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", -1)
                if (notificationId != -1) {
                    NotificationHelper.cancelNotification(context, notificationId)
                }
                // Snooze for 10 minutes
                AlarmScheduler.scheduleSnoozeAlarm(context, taskId, minutes = 10)
            }

            AlarmScheduler.ACTION_UPDATE_WIDGET -> {
                updateOngoingAndWidgets(context)
                AlarmScheduler.scheduleNextWidgetRefresh(context)
            }

            "com.example.ACTION_TEST_NOTIFICATION" -> {
                NotificationHelper.showTestNotification(context)
            }
        }
    }

    private fun updateOngoingAndWidgets(context: Context) {
        val prefsRepo = PreferencesRepository(context)
        val settings = prefsRepo.loadSettings()

        val (currentTask, nextTask) = ScheduleRepository.getCurrentAndNextTask(Calendar.getInstance())

        val now = Calendar.getInstance()
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val shouldShow = NotificationHelper.shouldDisplayOngoingNotification(settings, currentTask, nextTask, currentMinutes)

        if (shouldShow) {
            val diffMins = ((currentTask?.endMinutesOfDay ?: currentMinutes) - currentMinutes).coerceAtLeast(0)
            val remainingText = if (diffMins >= 60) {
                "${diffMins / 60}h ${diffMins % 60}p"
            } else {
                "${diffMins}p"
            }
            NotificationHelper.updateOngoingNotification(context, currentTask, nextTask, remainingText)
        } else {
            NotificationHelper.cancelOngoingNotification(context)
        }
        RoutineGlanceWidgetReceiver.updateAllWidgets(context)
    }
}
