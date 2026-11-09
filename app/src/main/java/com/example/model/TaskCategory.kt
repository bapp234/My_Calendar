package com.example.model

import androidx.compose.ui.graphics.Color

enum class TaskCategory(
    val id: String,
    val displayName: String,
    val emoji: String,
    val primaryColor: Color,
    val containerColor: Color,
    val onContainerColor: Color
) {
    BREAKFAST(
        id = "BREAKFAST",
        displayName = "Ăn sáng",
        emoji = "🍳",
        primaryColor = Color(0xFFF97316),
        containerColor = Color(0xFFFFEDD5),
        onContainerColor = Color(0xFF9A3412)
    ),
    LUNCH(
        id = "LUNCH",
        displayName = "Ăn trưa",
        emoji = "🍱",
        primaryColor = Color(0xFFEA580C),
        containerColor = Color(0xFFFFE4E6),
        onContainerColor = Color(0xFF9F1239)
    ),
    DINNER(
        id = "DINNER",
        displayName = "Ăn tối",
        emoji = "🍲",
        primaryColor = Color(0xFFD97706),
        containerColor = Color(0xFFFEF3C7),
        onContainerColor = Color(0xFF92400E)
    ),
    INTERNSHIP(
        id = "INTERNSHIP",
        displayName = "Thực tập",
        emoji = "💼",
        primaryColor = Color(0xFF2563EB),
        containerColor = Color(0xFFDBEAFE),
        onContainerColor = Color(0xFF1E40AF)
    ),
    THESIS(
        id = "THESIS",
        displayName = "Luận văn",
        emoji = "📚",
        primaryColor = Color(0xFF6366F1),
        containerColor = Color(0xFFE0E7FF),
        onContainerColor = Color(0xFF3730A3)
    ),
    TOEIC(
        id = "TOEIC",
        displayName = "TOEIC",
        emoji = "🎧",
        primaryColor = Color(0xFFF59E0B),
        containerColor = Color(0xFFFEF9C3),
        onContainerColor = Color(0xFF854D0E)
    ),
    COMMUTE(
        id = "COMMUTE",
        displayName = "Di chuyển",
        emoji = "🛵",
        primaryColor = Color(0xFF64748B),
        containerColor = Color(0xFFF1F5F9),
        onContainerColor = Color(0xFF334155)
    ),
    STRENGTH_TRAINING(
        id = "STRENGTH_TRAINING",
        displayName = "Tập sức mạnh",
        emoji = "🏋️‍♂️",
        primaryColor = Color(0xFF10B981),
        containerColor = Color(0xFFD1FAE5),
        onContainerColor = Color(0xFF065F46)
    ),
    FOOTBALL(
        id = "FOOTBALL",
        displayName = "Đá bóng",
        emoji = "⚽",
        primaryColor = Color(0xFFF43F5E),
        containerColor = Color(0xFFFFE4E6),
        onContainerColor = Color(0xFF9F1239)
    ),
    SKINCARE(
        id = "SKINCARE",
        displayName = "Chăm sóc da & tắm",
        emoji = "🧴",
        primaryColor = Color(0xFF06B6D4),
        containerColor = Color(0xFFCFFAFE),
        onContainerColor = Color(0xFF155E75)
    ),
    RELAX(
        id = "RELAX",
        displayName = "Nghỉ / Thư giãn",
        emoji = "☕",
        primaryColor = Color(0xFF8B5CF6),
        containerColor = Color(0xFFEDE9FE),
        onContainerColor = Color(0xFF5B21B6)
    ),
    SLEEP(
        id = "SLEEP",
        displayName = "Ngủ",
        emoji = "🌙",
        primaryColor = Color(0xFF334155),
        containerColor = Color(0xFFE2E8F0),
        onContainerColor = Color(0xFF0F172A)
    );

    companion object {
        fun fromId(id: String?): TaskCategory {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: RELAX
        }
    }
}
