package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.alarm.AlarmReceiver
import com.example.model.ScheduleItem
import com.example.model.TaskCategory
import com.example.util.ImageStorageManager

object NotificationHelper {

    const val CHANNEL_TASKS_HIGH = "channel_tasks_high_priority"
    const val CHANNEL_ONGOING = "channel_ongoing_task"

    const val NOTIFICATION_ID_ONGOING = 999
    const val NOTIFICATION_ID_TEST = 888

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // High Priority Channel for Reminders & Start-time alarms
            val highChannel = NotificationChannel(
                CHANNEL_TASKS_HIGH,
                "Nhắc nhở lịch trình (Báo thức)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Thông báo nhắc trước và đúng giờ bắt đầu các công việc trong ngày"
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 350, 200, 350)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                setSound(soundUri, null)
            }
            notificationManager.createNotificationChannel(highChannel)

            // Ongoing Channel for Currently Active Task
            val ongoingChannel = NotificationChannel(
                CHANNEL_ONGOING,
                "Công việc đang diễn ra",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Hiện việc đang diễn ra thường trực trên màn hình khóa và thanh thông báo"
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                enableVibration(false)
                enableLights(false)
            }
            notificationManager.createNotificationChannel(ongoingChannel)
        }
    }

    fun showTaskNotification(
        context: Context,
        task: ScheduleItem,
        isAdvanceReminder: Boolean,
        minutesAdvance: Int = 0
    ) {
        createNotificationChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = task.id.hashCode()

        // Content Intent: Opens MainActivity
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TASK_ID", task.id)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: "Xong" (Mark Complete)
        val completeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.ACTION_TASK_COMPLETE"
            putExtra("EXTRA_TASK_ID", task.id)
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 1000,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: "Báo lại 10 phút" (Snooze)
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.ACTION_TASK_SNOOZE"
            putExtra("EXTRA_TASK_ID", task.id)
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId + 2000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isAdvanceReminder) {
            "⏰ Nhắc trước $minutesAdvance phút: ${task.title}"
        } else {
            "🔔 Đến giờ: ${task.title}!"
        }

        val text = "Bắt đầu: ${task.startTimeFormatted} (${task.timeRangeFormatted})${if (task.description.isNotBlank()) " • " + task.description else ""}"

        // Get custom or rendered illustration bitmap
        val categoryBitmap = ImageStorageManager.getCategoryBitmap(context, task.category, width = 720, height = 400)

        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(categoryBitmap)
            .setSummaryText(text)

        val builder = NotificationCompat.Builder(context, CHANNEL_TASKS_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(bigPictureStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "✓ Xong", completePendingIntent)
            .addAction(0, "⏰ Báo lại 10 phút", snoozePendingIntent)

        notificationManager.notify(notificationId, builder.build())
    }

    fun showTestNotification(context: Context) {
        createNotificationChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_TEST,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.ACTION_TASK_COMPLETE"
            putExtra("EXTRA_TASK_ID", "test_task")
            putExtra("EXTRA_NOTIFICATION_ID", NOTIFICATION_ID_TEST)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID_TEST + 10,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.ACTION_TASK_SNOOZE"
            putExtra("EXTRA_TASK_ID", "test_task")
            putExtra("EXTRA_NOTIFICATION_ID", NOTIFICATION_ID_TEST)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID_TEST + 20,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val categoryBitmap = ImageStorageManager.getCategoryBitmap(context, TaskCategory.FOOTBALL, width = 720, height = 400)

        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(categoryBitmap)
            .setSummaryText("21:00 - 23:30 • Sân bóng đá (Thông báo thử nghiệm)")

        val builder = NotificationCompat.Builder(context, CHANNEL_TASKS_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔔 Thông báo thử: 21:00 Đá bóng")
            .setContentText("Thông báo đã sẵn sàng trên màn hình khóa với hình ảnh và các nút tiện ích!")
            .setStyle(bigPictureStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "✓ Xong", completePendingIntent)
            .addAction(0, "⏰ Báo lại 10 phút", snoozePendingIntent)

        notificationManager.notify(NOTIFICATION_ID_TEST, builder.build())
    }

    fun updateOngoingNotification(
        context: Context,
        currentTask: ScheduleItem?,
        nextTask: ScheduleItem?,
        remainingText: String
    ) {
        createNotificationChannels(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_ONGOING,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, subtitle, categoryBitmap) = if (currentTask != null) {
            val titleText = "Đang diễn ra: ${currentTask.title} (${currentTask.timeRangeFormatted})"
            val nextInfo = if (nextTask != null) "Tiếp theo: ${nextTask.title} lúc ${nextTask.startTimeFormatted}" else "Đã hết lịch hôm nay"
            val subText = "⏳ Còn $remainingText • $nextInfo"
            val bmp = ImageStorageManager.getCategoryBitmap(context, currentTask.category, width = 600, height = 300)
            Triple(titleText, subText, bmp)
        } else {
            val titleText = "🏖️ Thời gian tự do"
            val subText = if (nextTask != null) {
                "Sắp tới: ${nextTask.title} lúc ${nextTask.startTimeFormatted}"
            } else {
                "Đã hoàn thành toàn bộ lịch hôm nay 🎉"
            }
            val bmp = ImageStorageManager.getCategoryBitmap(context, nextTask?.category ?: TaskCategory.RELAX, width = 600, height = 300)
            Triple(titleText, subText, bmp)
        }

        val style = NotificationCompat.BigPictureStyle()
            .bigPicture(categoryBitmap)
            .setSummaryText(subtitle)

        val builder = NotificationCompat.Builder(context, CHANNEL_ONGOING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setStyle(style)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(contentPendingIntent)

        notificationManager.notify(NOTIFICATION_ID_ONGOING, builder.build())
    }

    fun shouldDisplayOngoingNotification(
        settings: com.example.model.AppSettings,
        currentTask: ScheduleItem?,
        nextTask: ScheduleItem?,
        currentMinutesOfDay: Int
    ): Boolean {
        if (!settings.ongoingNotificationEnabled) return false

        if (currentTask != null) {
            // Check category filtering
            if (!settings.isCategoryIncludedInOngoing(currentTask.category.name)) {
                return false
            }

            return when (settings.ongoingDisplayMode) {
                com.example.model.OngoingDisplayMode.ALWAYS -> true
                com.example.model.OngoingDisplayMode.BEFORE_END -> {
                    val endMins = currentTask.endMinutesOfDay
                    val diffMins = endMins - currentMinutesOfDay
                    diffMins in 0..settings.ongoingBeforeEndMinutes
                }
                com.example.model.OngoingDisplayMode.BEFORE_NEXT -> {
                    if (nextTask != null) {
                        val nextStartMins = if (nextTask.startMinutesOfDay < currentMinutesOfDay) {
                            nextTask.startMinutesOfDay + 24 * 60
                        } else {
                            nextTask.startMinutesOfDay
                        }
                        val diffMins = nextStartMins - currentMinutesOfDay
                        diffMins in 0..settings.ongoingBeforeNextMinutes
                    } else {
                        false
                    }
                }
            }
        } else {
            // Free time / gap
            return when (settings.ongoingDisplayMode) {
                com.example.model.OngoingDisplayMode.ALWAYS -> true
                com.example.model.OngoingDisplayMode.BEFORE_END -> false
                com.example.model.OngoingDisplayMode.BEFORE_NEXT -> {
                    if (nextTask != null && settings.isCategoryIncludedInOngoing(nextTask.category.name)) {
                        val nextStartMins = if (nextTask.startMinutesOfDay < currentMinutesOfDay) {
                            nextTask.startMinutesOfDay + 24 * 60
                        } else {
                            nextTask.startMinutesOfDay
                        }
                        val diffMins = nextStartMins - currentMinutesOfDay
                        diffMins in 0..settings.ongoingBeforeNextMinutes
                    } else {
                        false
                    }
                }
            }
        }
    }

    fun cancelOngoingNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID_ONGOING)
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}
