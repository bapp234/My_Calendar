package com.example.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Primary 4x2 Widget Receiver
class RoutineGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoutineGlanceWidget()

    companion object {
        private const val TAG = "RoutineGlanceReceiver"

        fun updateAllWidgets(context: Context) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                try {
                    val manager = GlanceAppWidgetManager(context)
                    val appWidgetManager = AppWidgetManager.getInstance(context)

                    // Safely update all 4x2 widgets
                    updateGlanceWidgetsSafely(context, manager, appWidgetManager, RoutineGlanceWidget())

                    // Safely update all 2x2 widgets
                    updateGlanceWidgetsSafely(context, manager, appWidgetManager, RoutineGlanceSmallWidget())
                } catch (e: Exception) {
                    Log.e(TAG, "Error in updateAllWidgets", e)
                }
            }
        }

        private suspend fun updateGlanceWidgetsSafely(
            context: Context,
            glanceManager: GlanceAppWidgetManager,
            appWidgetManager: AppWidgetManager,
            widget: GlanceAppWidget
        ) {
            try {
                val glanceIds = glanceManager.getGlanceIds(widget::class.java)
                for (id in glanceIds) {
                    try {
                        val appWidgetId = glanceManager.getAppWidgetId(id)
                        // Only trigger update if the widget ID is valid and registered with AppWidgetManager
                        if (appWidgetManager.getAppWidgetInfo(appWidgetId) != null) {
                            widget.update(context, id)
                        } else {
                            Log.d(TAG, "Skipping inactive or deleted appWidgetId: $appWidgetId")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Skipping glanceId $id due to error", e)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error querying glance ids for ${widget::class.java.simpleName}", e)
            }
        }
    }
}

// Primary 2x2 Widget Receiver
class RoutineGlanceSmallWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoutineGlanceSmallWidget()
}

// Backward compatibility alias: ensures previously added home screen widget (e.g. appWidgetId 4)
// resolves its provider info instead of throwing "App widget provider info is null"
class RoutineWidgetProvider : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoutineGlanceWidget()
}

class RoutineWidgetSmallProvider : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = RoutineGlanceSmallWidget()
}
