package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.example.model.AppSettings
import com.example.model.CategorySetting
import com.example.model.OngoingDisplayMode
import com.example.model.OngoingTimeOptions
import com.example.model.OngoingUpdateFrequency
import com.example.model.ReminderOptions
import com.example.model.TaskCategory
import com.example.util.ImageStorageManager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onUpdateGlobalReminder: (Int) -> Unit,
    onUpdateOngoingNotification: (Boolean) -> Unit,
    onUpdateOngoingDisplayMode: (OngoingDisplayMode) -> Unit,
    onUpdateOngoingBeforeEndMinutes: (Int) -> Unit,
    onUpdateOngoingBeforeNextMinutes: (Int) -> Unit,
    onUpdateOngoingUpdateFrequency: (OngoingUpdateFrequency) -> Unit,
    onToggleOngoingCategory: (String) -> Unit,
    onSelectAllOngoingCategories: (Boolean) -> Unit,
    onUpdateCategorySetting: (categoryId: String, enabled: Boolean, reminderMinutes: Int) -> Unit,
    onSaveCategoryPhoto: (categoryId: String, uri: Uri) -> Unit,
    onDeleteCategoryPhoto: (categoryId: String) -> Unit,
    onSendTestNotification: () -> Unit,
    onTriggerOngoingNotificationNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showGlobalReminderDialog by remember { mutableStateOf(false) }
    var selectedCategoryForReminderDialog by remember { mutableStateOf<TaskCategory?>(null) }
    var targetCategoryForPhoto by remember { mutableStateOf<TaskCategory?>(null) }

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null && targetCategoryForPhoto != null) {
            onSaveCategoryPhoto(targetCategoryForPhoto!!.name, uri)
            Toast.makeText(context, "Đã cập nhật ảnh cho ${targetCategoryForPhoto!!.displayName}", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Test Notification Button Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Thử nghiệm thông báo đẩy",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Xem ngay thông báo màn hình khóa với ảnh lớn & 2 nút bấm",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onSendTestNotification()
                            Toast.makeText(context, "Đã gửi thông báo thử! Hãy kiểm tra thanh thông báo hoặc màn hình khóa", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_test_notification_button")
                    ) {
                        Text("Gửi thông báo thử", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Battery Optimization & Exact Alarms Guide
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BatteryAlert,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B)
                        )
                        Text(
                            text = "Đảm bảo thông báo khi đóng app",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Text(
                        text = "Để Android không tắt báo thức khi app chạy ngầm hoặc khóa màn hình, vui lòng tắt Tối ưu hóa pin và cấp quyền Báo thức chính xác.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    val intent = Intent(Settings.ACTION_SETTINGS)
                                    context.startActivity(intent)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Tối ưu pin", fontSize = 13.sp)
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            OutlinedButton(
                                onClick = {
                                    try {
                                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                            data = Uri.parse("package:${context.packageName}")
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val intent = Intent(Settings.ACTION_SETTINGS)
                                        context.startActivity(intent)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Báo thức chính xác", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. Global Reminder Timing Setting
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Cài đặt thông báo chung",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Global advance reminder option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showGlobalReminderDialog = true },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Thời gian nhắc trước mặc định",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = ReminderOptions.getLabel(settings.globalReminderMinutes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Thay đổi",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Ongoing lockscreen notification toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = "Thông báo thường trực (ongoing)",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "Luôn ghim việc đang diễn ra kèm ảnh minh họa trên màn hình khóa",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = settings.ongoingNotificationEnabled,
                            onCheckedChange = { onUpdateOngoingNotification(it) },
                            modifier = Modifier.testTag("ongoing_notification_switch")
                        )
                    }

                    if (settings.ongoingNotificationEnabled) {
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // 1. Kiểu hiển thị
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Kiểu hiển thị thông báo",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Option: Luôn hiện
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUpdateOngoingDisplayMode(OngoingDisplayMode.ALWAYS) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.ongoingDisplayMode == OngoingDisplayMode.ALWAYS,
                                    onClick = { onUpdateOngoingDisplayMode(OngoingDisplayMode.ALWAYS) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Luôn hiện", fontWeight = FontWeight.SemiBold)
                                    Text("Hiển thị liên tục việc đang diễn ra hoặc thời gian tự do", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Option: Chỉ hiện trong X phút trước giờ kết thúc việc đang diễn ra
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUpdateOngoingDisplayMode(OngoingDisplayMode.BEFORE_END) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.ongoingDisplayMode == OngoingDisplayMode.BEFORE_END,
                                    onClick = { onUpdateOngoingDisplayMode(OngoingDisplayMode.BEFORE_END) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Chỉ hiện trước giờ kết thúc việc đang làm", fontWeight = FontWeight.SemiBold)
                                    Text("Chỉ xuất hiện khi sắp hết giờ của việc hiện tại", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            if (settings.ongoingDisplayMode == OngoingDisplayMode.BEFORE_END) {
                                Row(
                                    modifier = Modifier.padding(start = 40.dp, top = 2.dp, bottom = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OngoingTimeOptions.BEFORE_END_OPTIONS.forEach { mins ->
                                        FilterChip(
                                            selected = settings.ongoingBeforeEndMinutes == mins,
                                            onClick = { onUpdateOngoingBeforeEndMinutes(mins) },
                                            label = { Text("Trước $mins phút") }
                                        )
                                    }
                                }
                            }

                            // Option: Chỉ hiện khi sắp đến việc tiếp theo
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUpdateOngoingDisplayMode(OngoingDisplayMode.BEFORE_NEXT) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = settings.ongoingDisplayMode == OngoingDisplayMode.BEFORE_NEXT,
                                    onClick = { onUpdateOngoingDisplayMode(OngoingDisplayMode.BEFORE_NEXT) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Chỉ hiện khi sắp đến việc tiếp theo", fontWeight = FontWeight.SemiBold)
                                    Text("Chỉ xuất hiện khi sắp bắt đầu công việc kế tiếp", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            if (settings.ongoingDisplayMode == OngoingDisplayMode.BEFORE_NEXT) {
                                Row(
                                    modifier = Modifier.padding(start = 40.dp, top = 2.dp, bottom = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OngoingTimeOptions.BEFORE_NEXT_OPTIONS.forEach { mins ->
                                        FilterChip(
                                            selected = settings.ongoingBeforeNextMinutes == mins,
                                            onClick = { onUpdateOngoingBeforeNextMinutes(mins) },
                                            label = { Text("Trước $mins phút") }
                                        )
                                    }
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // 2. Tần suất cập nhật đồng hồ đếm ngược
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Tần suất cập nhật đếm ngược",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Cập nhật hoàn toàn im lặng (setOnlyAlertOnce: không rung hay đổ chuông lại)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OngoingUpdateFrequency.entries.forEach { freq ->
                                    FilterChip(
                                        selected = settings.ongoingUpdateFrequency == freq,
                                        onClick = { onUpdateOngoingUpdateFrequency(freq) },
                                        label = { Text(freq.label, fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // 3. Loại việc được hiện trong thông báo thường trực
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Loại việc được hiện",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Chọn việc bạn muốn ghim (${settings.ongoingIncludedCategories.size}/${TaskCategory.entries.size} nhóm)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row {
                                    TextButton(onClick = { onSelectAllOngoingCategories(true) }) {
                                        Text("Tất cả", fontSize = 12.sp)
                                    }
                                    TextButton(onClick = { onSelectAllOngoingCategories(false) }) {
                                        Text("Bỏ chọn", fontSize = 12.sp)
                                    }
                                }
                            }

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TaskCategory.entries.forEach { category ->
                                    val isSelected = settings.isCategoryIncludedInOngoing(category.name)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onToggleOngoingCategory(category.name) },
                                        label = {
                                            Text("${category.emoji} ${category.displayName}", fontSize = 12.sp)
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = category.containerColor,
                                            selectedLabelColor = category.onContainerColor
                                        )
                                    )
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Test / Preview Button
                        Button(
                            onClick = {
                                onTriggerOngoingNotificationNow()
                                Toast.makeText(context, "Đã áp dụng cài đặt thông báo thường trực ngay!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("trigger_ongoing_notification_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Áp dụng & Cập nhật thông báo ngay")
                        }
                    }
                }
            }
        }

        // Section header for 12 Categories
        item {
            Text(
                text = "Cài đặt & Ảnh minh họa theo từng việc (12 nhóm)",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 4. List of 12 categories with Photo Picker & Reminder Override
        items(TaskCategory.entries) { category ->
            val catSetting = settings.categorySettings[category.name] ?: CategorySetting(category.name)
            val customPath = ImageStorageManager.getCustomImagePath(context, category.name)

            CategorySettingCard(
                category = category,
                setting = catSetting,
                customImagePath = customPath,
                globalReminderMinutes = settings.globalReminderMinutes,
                onToggleEnabled = { enabled ->
                    onUpdateCategorySetting(category.name, enabled, catSetting.reminderMinutes)
                },
                onChangeReminder = {
                    selectedCategoryForReminderDialog = category
                },
                onPickPhoto = {
                    targetCategoryForPhoto = category
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onDeletePhoto = {
                    onDeleteCategoryPhoto(category.name)
                    Toast.makeText(context, "Đã xóa ảnh tùy chỉnh của ${category.displayName}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Dialog: Global Reminder Picker
    if (showGlobalReminderDialog) {
        AlertDialog(
            onDismissRequest = { showGlobalReminderDialog = false },
            title = { Text("Chọn thời gian nhắc trước chung") },
            text = {
                Column {
                    ReminderOptions.OPTIONS.forEach { minutes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateGlobalReminder(minutes)
                                    showGlobalReminderDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.globalReminderMinutes == minutes,
                                onClick = {
                                    onUpdateGlobalReminder(minutes)
                                    showGlobalReminderDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = ReminderOptions.getLabel(minutes))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGlobalReminderDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }

    // Dialog: Per-Category Reminder Picker
    if (selectedCategoryForReminderDialog != null) {
        val cat = selectedCategoryForReminderDialog!!
        val currentSetting = settings.categorySettings[cat.name] ?: CategorySetting(cat.name)
        val optionsWithDefault = listOf(-1) + ReminderOptions.OPTIONS

        AlertDialog(
            onDismissRequest = { selectedCategoryForReminderDialog = null },
            title = { Text("Thời gian nhắc: ${cat.displayName}") },
            text = {
                Column {
                    optionsWithDefault.forEach { minutes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUpdateCategorySetting(cat.name, currentSetting.enabled, minutes)
                                    selectedCategoryForReminderDialog = null
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentSetting.reminderMinutes == minutes,
                                onClick = {
                                    onUpdateCategorySetting(cat.name, currentSetting.enabled, minutes)
                                    selectedCategoryForReminderDialog = null
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = ReminderOptions.getLabel(minutes))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedCategoryForReminderDialog = null }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
fun CategorySettingCard(
    category: TaskCategory,
    setting: CategorySetting,
    customImagePath: String?,
    globalReminderMinutes: Int,
    onToggleEnabled: (Boolean) -> Unit,
    onChangeReminder: () -> Unit,
    onPickPhoto: () -> Unit,
    onDeletePhoto: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(category.containerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = category.emoji, fontSize = 20.sp)
                    }

                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Switch(
                    checked = setting.enabled,
                    onCheckedChange = { onToggleEnabled(it) },
                    modifier = Modifier.testTag("cat_switch_${category.name}")
                )
            }

            // Photo Preview & Picker Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Thumbnail
                    if (customImagePath != null) {
                        AsyncImage(
                            model = File(customImagePath),
                            contentDescription = "Ảnh ${category.displayName}",
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, category.primaryColor, RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(category.containerColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = category.emoji, fontSize = 24.sp)
                        }
                    }

                    Column {
                        Text(
                            text = if (customImagePath != null) "Ảnh tùy chọn (Thư viện)" else "Ảnh minh họa vẽ sẵn",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = if (customImagePath != null) "Hiện trên thông báo & Widget" else "Chạm 'Đổi ảnh' để chọn từ máy",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onPickPhoto,
                        modifier = Modifier.testTag("pick_photo_${category.name}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Chọn ảnh mới",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (customImagePath != null) {
                        IconButton(
                            onClick = onDeletePhoto,
                            modifier = Modifier.testTag("delete_photo_${category.name}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Xóa ảnh tùy chỉnh",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Reminder timing row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChangeReminder() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Thời gian nhắc nhở:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val effectiveLabel = if (setting.reminderMinutes >= 0) {
                    ReminderOptions.getLabel(setting.reminderMinutes)
                } else {
                    "Mặc định (${ReminderOptions.getLabel(globalReminderMinutes)})"
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = category.containerColor
                ) {
                    Text(
                        text = effectiveLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = category.onContainerColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
