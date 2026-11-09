package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarm.AlarmScheduler
import com.example.notification.NotificationHelper
import com.example.ui.ScheduleViewModel
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.screens.WeekScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.widget.RoutineGlanceWidgetReceiver
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channels
        NotificationHelper.createNotificationChannels(this)

        // Reschedule alarms and update widgets
        AlarmScheduler.rescheduleAllAlarms(this)
        RoutineGlanceWidgetReceiver.updateAllWidgets(this)

        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: ScheduleViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    // Request POST_NOTIFICATIONS permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            AlarmScheduler.rescheduleAllAlarms(context)
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val currentTaskState by viewModel.currentTaskState.collectAsState()
    val completedTaskIds by viewModel.completedTaskIds.collectAsState()
    val selectedDayOfWeek by viewModel.selectedDayOfWeek.collectAsState()
    val settings by viewModel.settings.collectAsState()

    // Formatted current date in Vietnamese
    val vietnameseDateFormat = SimpleDateFormat("EEEE, d 'tháng' M, yyyy", Locale("vi", "VN"))
    val todayFormattedDate = vietnameseDateFormat.format(Date()).replaceFirstChar { it.uppercase() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lịch của tôi",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 21.sp
                            )
                        )
                        Text(
                            text = todayFormattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.Today else Icons.Outlined.Today,
                            contentDescription = "Hôm nay"
                        )
                    },
                    label = { Text("Hôm nay", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_tab_today")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "Cả tuần"
                        )
                    },
                    label = { Text("Cả tuần", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_tab_week")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Cài đặt"
                        )
                    },
                    label = { Text("Cài đặt", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> TodayScreen(
                    currentTaskState = currentTaskState,
                    completedTaskIds = completedTaskIds,
                    onToggleTask = { viewModel.toggleTaskCompletion(it) },
                    onResetToday = { viewModel.resetTodayTasks() }
                )
                1 -> WeekScreen(
                    selectedDayOfWeek = selectedDayOfWeek,
                    onSelectDayOfWeek = { viewModel.setSelectedDayOfWeek(it) }
                )
                2 -> SettingsScreen(
                    settings = settings,
                    onUpdateGlobalReminder = { viewModel.updateGlobalReminderMinutes(it) },
                    onUpdateOngoingNotification = { viewModel.updateOngoingNotificationEnabled(it) },
                    onUpdateOngoingDisplayMode = { viewModel.updateOngoingDisplayMode(it) },
                    onUpdateOngoingBeforeEndMinutes = { viewModel.updateOngoingBeforeEndMinutes(it) },
                    onUpdateOngoingBeforeNextMinutes = { viewModel.updateOngoingBeforeNextMinutes(it) },
                    onUpdateOngoingUpdateFrequency = { viewModel.updateOngoingUpdateFrequency(it) },
                    onToggleOngoingCategory = { viewModel.toggleOngoingCategory(it) },
                    onSelectAllOngoingCategories = { viewModel.selectAllOngoingCategories(it) },
                    onUpdateCategorySetting = { catId, enabled, reminder ->
                        viewModel.updateCategorySetting(catId, enabled, reminder)
                    },
                    onSaveCategoryPhoto = { catId, uri ->
                        viewModel.saveCategoryPhoto(catId, uri)
                    },
                    onDeleteCategoryPhoto = { catId ->
                        viewModel.deleteCategoryPhoto(catId)
                    },
                    onSendTestNotification = { viewModel.sendTestNotification() },
                    onTriggerOngoingNotificationNow = { viewModel.triggerOngoingNotificationNow() }
                )
            }
        }
    }
}
