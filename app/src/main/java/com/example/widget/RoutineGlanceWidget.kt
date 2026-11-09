package com.example.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.data.ScheduleRepository
import com.example.model.ScheduleItem
import kotlinx.coroutines.CancellationException
import java.util.Calendar

sealed interface WidgetUiState {
    data class Success(val currentTask: ScheduleItem?, val nextTask: ScheduleItem?) : WidgetUiState
    object Error : WidgetUiState
}

class RoutineGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val uiState: WidgetUiState = try {
            val (current, next) = ScheduleRepository.getCurrentAndNextTask(Calendar.getInstance())
            WidgetUiState.Success(current, next)
        } catch (e: Exception) {
            Log.e("RoutineGlanceWidget", "Error loading schedule for glance widget", e)
            WidgetUiState.Error
        }

        try {
            provideContent {
                GlanceTheme {
                    when (uiState) {
                        is WidgetUiState.Success -> MainWidgetLayout(uiState.currentTask, uiState.nextTask)
                        WidgetUiState.Error -> ErrorFallbackWidget()
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            Log.e("RoutineGlanceWidget", "Error in provideContent", t)
        }
    }

    @Composable
    private fun MainWidgetLayout(currentTask: ScheduleItem?, nextTask: ScheduleItem?) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(18.dp)
                .background(ColorProvider(Color(0xFF0F172A)))
                .padding(14.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                // Header: Status Badge & Time
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .cornerRadius(6.dp)
                            .background(ColorProvider(Color(0xFF0284C7)))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (currentTask != null) "ĐANG DIỄN RA" else "TỰ DO",
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = GlanceModifier.width(8.dp))

                    Text(
                        text = currentTask?.timeRangeFormatted ?: "Hôm nay",
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF38BDF8)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Current Task Title
                Text(
                    text = if (currentTask != null) {
                        "${currentTask.category.emoji} ${currentTask.title}"
                    } else {
                        "🏖️ Thời gian tự do"
                    },
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2
                )

                if (currentTask != null && currentTask.description.isNotBlank()) {
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = currentTask.description,
                        style = TextStyle(
                            color = ColorProvider(Color(0xFF94A3B8)),
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Next Task Info Box
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .cornerRadius(8.dp)
                        .background(ColorProvider(Color(0xFF1E293B)))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (nextTask != null) {
                            "Tiếp: ${nextTask.title} (${nextTask.startTimeFormatted})"
                        } else {
                            "🎉 Đã xong lịch hôm nay"
                        },
                        style = TextStyle(
                            color = ColorProvider(Color(0xFFE2E8F0)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }

    @Composable
    private fun ErrorFallbackWidget() {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(16.dp)
                .background(ColorProvider(Color(0xFF1E293B)))
                .padding(16.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Lịch của tôi",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "Lỗi tải - Chạm để mở app",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFEF4444)),
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}
