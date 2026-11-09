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
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.data.ScheduleRepository
import com.example.model.ScheduleItem
import kotlinx.coroutines.CancellationException
import java.util.Calendar

class RoutineGlanceSmallWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val uiState: WidgetUiState = try {
            val (current, next) = ScheduleRepository.getCurrentAndNextTask(Calendar.getInstance())
            WidgetUiState.Success(current, next)
        } catch (e: Exception) {
            Log.e("RoutineGlanceSmallWidget", "Error loading schedule for small widget", e)
            WidgetUiState.Error
        }

        try {
            provideContent {
                GlanceTheme {
                    when (uiState) {
                        is WidgetUiState.Success -> MainSmallWidgetLayout(uiState.currentTask, uiState.nextTask)
                        WidgetUiState.Error -> ErrorFallbackWidget()
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            Log.e("RoutineGlanceSmallWidget", "Error in provideContent", t)
        }
    }

    @Composable
    private fun MainSmallWidgetLayout(currentTask: ScheduleItem?, nextTask: ScheduleItem?) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(18.dp)
                .background(ColorProvider(Color(0xFF0F172A)))
                .padding(10.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                Text(
                    text = currentTask?.timeRangeFormatted ?: "Hôm nay",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF38BDF8)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                Text(
                    text = if (currentTask != null) {
                        "${currentTask.category.emoji} ${currentTask.title}"
                    } else {
                        "🏖️ Tự do"
                    },
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                Text(
                    text = if (nextTask != null) {
                        "Tiếp: ${nextTask.title}"
                    } else {
                        "Hết lịch"
                    },
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF94A3B8)),
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }

    @Composable
    private fun ErrorFallbackWidget() {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(14.dp)
                .background(ColorProvider(Color(0xFF1E293B)))
                .padding(10.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Lỗi tải",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFEF4444)),
                    fontSize = 12.sp
                )
            )
        }
    }
}
