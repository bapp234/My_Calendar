package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.PreferencesRepository
import com.example.data.ScheduleRepository
import com.example.model.ScheduleItem
import java.util.Calendar

object AlarmScheduler {

    private const val TAG = "AlarmScheduler"

    const val ACTION_TASK_ALARM = "com.example.ACTION_TASK_ALARM"
    const val ACTION_TASK_REMINDER = "com.example.ACTION_TASK_REMINDER"
    const val ACTION_UPDATE_WIDGET = "com.example.ACTION_UPDATE_WIDGET"

    fun canScheduleExact(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun rescheduleAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val prefsRepo = PreferencesRepository(context)
        val settings = prefsRepo.loadSettings()
        val allItems = ScheduleRepository.getAllItems()

        Log.d(TAG, "Rescheduling all alarms. Items count: ${allItems.size}")

        for (item in allItems) {
            val categoryId = item.category.name
            val isEnabled = settings.isCategoryNotificationEnabled(categoryId)
            val reminderMinutes = settings.getEffectiveReminderMinutes(categoryId)

            // 1. Exact start time alarm
            if (isEnabled) {
                scheduleExactAlarm(context, alarmManager, item, isAdvance = false, advanceMinutes = 0)
            } else {
                cancelAlarm(context, alarmManager, item, isAdvance = false)
            }

            // 2. Advance reminder alarm
            if (isEnabled && reminderMinutes > 0) {
                scheduleExactAlarm(context, alarmManager, item, isAdvance = true, advanceMinutes = reminderMinutes)
            } else {
                cancelAlarm(context, alarmManager, item, isAdvance = true)
            }
        }

        // Schedule widget & ongoing notification updates
        scheduleNextWidgetRefresh(context, alarmManager)
    }

    private fun scheduleExactAlarm(
        context: Context,
        alarmManager: AlarmManager,
        item: ScheduleItem,
        isAdvance: Boolean,
        advanceMinutes: Int
    ) {
        val now = Calendar.getInstance()
        val targetCalendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, item.dayOfWeek)
            set(Calendar.HOUR_OF_DAY, item.startHour)
            set(Calendar.MINUTE, item.startMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (isAdvance) {
                add(Calendar.MINUTE, -advanceMinutes)
            }
        }

        // If time already passed this week, schedule for next week
        if (targetCalendar.timeInMillis <= now.timeInMillis) {
            targetCalendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        val action = if (isAdvance) ACTION_TASK_REMINDER else ACTION_TASK_ALARM
        val requestCode = item.id.hashCode() + (if (isAdvance) 10000 else 0)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
            putExtra("EXTRA_TASK_ID", item.id)
            putExtra("EXTRA_IS_ADVANCE", isAdvance)
            putExtra("EXTRA_ADVANCE_MINUTES", advanceMinutes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canScheduleExact(context)) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetCalendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetCalendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    targetCalendar.timeInMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Scheduled alarm for ${item.title} ($action) at ${targetCalendar.time}")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException while scheduling exact alarm", e)
        }
    }

    private fun cancelAlarm(
        context: Context,
        alarmManager: AlarmManager,
        item: ScheduleItem,
        isAdvance: Boolean
    ) {
        val action = if (isAdvance) ACTION_TASK_REMINDER else ACTION_TASK_ALARM
        val requestCode = item.id.hashCode() + (if (isAdvance) 10000 else 0)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun scheduleSnoozeAlarm(context: Context, taskId: String, minutes: Int = 10) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerAtMillis = System.currentTimeMillis() + (minutes * 60 * 1000L)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TASK_ALARM
            putExtra("EXTRA_TASK_ID", taskId)
            putExtra("EXTRA_IS_SNOOZE", true)
        }

        val requestCode = taskId.hashCode() + 50000
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (canScheduleExact(context)) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Error scheduling snooze", e)
        }
    }

    fun scheduleNextWidgetRefresh(context: Context, alarmManager: AlarmManager? = null) {
        val am = alarmManager ?: (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
        val triggerAtMillis = System.currentTimeMillis() + (60 * 1000L) // Refresh every 1 minute or at next task boundary

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_UPDATE_WIDGET
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            99999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setAndAllowWhileIdle(AlarmManager.RTC, triggerAtMillis, pendingIntent)
            } else {
                am.set(AlarmManager.RTC, triggerAtMillis, pendingIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
