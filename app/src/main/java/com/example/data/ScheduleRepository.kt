package com.example.data

import com.example.model.ScheduleItem
import com.example.model.TaskCategory
import java.util.Calendar

object ScheduleRepository {

    fun getAllItems(): List<ScheduleItem> {
        val list = mutableListOf<ScheduleItem>()
        val days = listOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
        )
        for (day in days) {
            list.addAll(getItemsForDay(day))
        }
        return list
    }

    fun getItemsForDay(dayOfWeek: Int): List<ScheduleItem> {
        val dayName = getDayName(dayOfWeek)
        return when (dayOfWeek) {
            Calendar.MONDAY -> getMondaySchedule(dayName)
            Calendar.TUESDAY -> getTuesdaySchedule(dayName)
            Calendar.WEDNESDAY -> getWednesdaySchedule(dayName)
            Calendar.THURSDAY -> getThursdaySchedule(dayName)
            Calendar.FRIDAY -> getFridaySchedule(dayName)
            Calendar.SATURDAY -> getSaturdaySchedule(dayName)
            Calendar.SUNDAY -> getSundaySchedule(dayName)
            else -> emptyList()
        }
    }

    fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.MONDAY -> "Thứ 2"
            Calendar.TUESDAY -> "Thứ 3"
            Calendar.WEDNESDAY -> "Thứ 4"
            Calendar.THURSDAY -> "Thứ 5"
            Calendar.FRIDAY -> "Thứ 6"
            Calendar.SATURDAY -> "Thứ 7"
            Calendar.SUNDAY -> "Chủ nhật"
            else -> "Thứ 2"
        }
    }

    // Thứ 2 (Bắt đầu lúc 06:15 và kết thúc 24:00 lúc di chuyển về)
    private fun getMondaySchedule(dayName: String): List<ScheduleItem> = listOf(
        ScheduleItem("mon_1", Calendar.MONDAY, dayName, 6, 15, 7, 0, "Dậy, chăm sóc da, ăn sáng", "Khởi đầu ngày mới năng lượng", TaskCategory.BREAKFAST),
        ScheduleItem("mon_2", Calendar.MONDAY, dayName, 7, 0, 8, 0, "Di chuyển, nghe TOEIC", "Luyện nghe thụ động trên đường", TaskCategory.COMMUTE),
        ScheduleItem("mon_3", Calendar.MONDAY, dayName, 8, 0, 12, 0, "Thực tập", "Tập trung công việc tại cơ quan", TaskCategory.INTERNSHIP),
        ScheduleItem("mon_4", Calendar.MONDAY, dayName, 12, 0, 13, 0, "Ăn trưa, ngủ trưa 20 phút", "Nạp năng lượng và chợp mắt 20p", TaskCategory.LUNCH),
        ScheduleItem("mon_5", Calendar.MONDAY, dayName, 13, 0, 17, 0, "Thực tập", "Hoàn thành nhiệm vụ buổi chiều", TaskCategory.INTERNSHIP),
        ScheduleItem("mon_6", Calendar.MONDAY, dayName, 17, 0, 18, 30, "Về, từ vựng TOEIC", "Ôn từ vựng trên đường về nhà", TaskCategory.TOEIC),
        ScheduleItem("mon_7", Calendar.MONDAY, dayName, 18, 30, 19, 0, "Ăn tối nhẹ", "Ăn nhẹ trước khi học và đá bóng", TaskCategory.DINNER),
        ScheduleItem("mon_8", Calendar.MONDAY, dayName, 19, 0, 20, 15, "Luận văn", "Nghiên cứu và viết luận văn", TaskCategory.THESIS),
        ScheduleItem("mon_9", Calendar.MONDAY, dayName, 20, 15, 20, 30, "Chuẩn bị đồ đá bóng", "Trang phục, giày, nước uống", TaskCategory.FOOTBALL),
        ScheduleItem("mon_10", Calendar.MONDAY, dayName, 20, 30, 21, 0, "Di chuyển đến sân", "Khởi hành sớm để khởi động", TaskCategory.COMMUTE),
        ScheduleItem("mon_11", Calendar.MONDAY, dayName, 21, 0, 23, 30, "Đá bóng", "Thi đấu hết mình, rèn thể lực", TaskCategory.FOOTBALL),
        ScheduleItem("mon_12", Calendar.MONDAY, dayName, 23, 30, 0, 0, "Di chuyển về", "Về nhà an toàn sau trận đấu", TaskCategory.COMMUTE)
    )

    // Thứ 3: 00:00 & 00:30 thuộc đêm Thứ 2 sang Thứ 3
    private fun getTuesdaySchedule(dayName: String): List<ScheduleItem> = listOf(
        ScheduleItem("tue_0a", Calendar.TUESDAY, dayName, 0, 0, 0, 30, "Tắm, chăm sóc da, sữa chua", "Hồi phục sau trận đá bóng đêm T2", TaskCategory.SKINCARE, crossMidnightNote = "Đêm T2 sang T3"),
        ScheduleItem("tue_0b", Calendar.TUESDAY, dayName, 0, 30, 6, 15, "Ngủ", "Giấc ngủ phục hồi", TaskCategory.SLEEP, crossMidnightNote = "Đêm T2 sang T3"),
        ScheduleItem("tue_1", Calendar.TUESDAY, dayName, 6, 15, 7, 0, "Dậy, chăm sóc da, ăn sáng", "Khởi đầu ngày mới", TaskCategory.BREAKFAST),
        ScheduleItem("tue_2", Calendar.TUESDAY, dayName, 7, 0, 8, 0, "Di chuyển, nghe TOEIC", "Luyện nghe TOEIC Part 1-4", TaskCategory.COMMUTE),
        ScheduleItem("tue_3", Calendar.TUESDAY, dayName, 8, 0, 12, 0, "Thực tập", "Công việc chuyên môn", TaskCategory.INTERNSHIP),
        ScheduleItem("tue_4", Calendar.TUESDAY, dayName, 12, 0, 13, 0, "Ăn trưa", "Nghỉ ngơi ăn trưa", TaskCategory.LUNCH),
        ScheduleItem("tue_5", Calendar.TUESDAY, dayName, 13, 0, 17, 0, "Thực tập", "Nhiệm vụ buổi chiều", TaskCategory.INTERNSHIP),
        ScheduleItem("tue_6", Calendar.TUESDAY, dayName, 17, 0, 18, 30, "Về, từ vựng TOEIC", "Ôn từ vựng flashcard", TaskCategory.TOEIC),
        ScheduleItem("tue_7", Calendar.TUESDAY, dayName, 18, 30, 19, 15, "Ăn tối", "Ăn tối đầy đủ dinh dưỡng", TaskCategory.DINNER),
        ScheduleItem("tue_8", Calendar.TUESDAY, dayName, 19, 15, 21, 15, "Luận văn", "Viết và chỉnh sửa chương luận văn", TaskCategory.THESIS),
        ScheduleItem("tue_9", Calendar.TUESDAY, dayName, 21, 15, 22, 0, "Tập sức mạnh (Buổi A thân trên)", "Ngực, lưng, vai, tay", TaskCategory.STRENGTH_TRAINING),
        ScheduleItem("tue_10", Calendar.TUESDAY, dayName, 22, 0, 22, 30, "Tắm, chăm sóc da", "Vệ sinh cá nhân, dưỡng ẩm", TaskCategory.SKINCARE),
        ScheduleItem("tue_11", Calendar.TUESDAY, dayName, 22, 30, 22, 45, "Thư giãn", "Đọc sách hoặc nghe nhạc nhẹ", TaskCategory.RELAX),
        ScheduleItem("tue_12", Calendar.TUESDAY, dayName, 22, 45, 6, 15, "Ngủ", "Ngủ sâu đúng giờ", TaskCategory.SLEEP)
    )

    // Thứ 4 (Tương tự Thứ 2)
    private fun getWednesdaySchedule(dayName: String): List<ScheduleItem> = listOf(
        ScheduleItem("wed_1", Calendar.WEDNESDAY, dayName, 6, 15, 7, 0, "Dậy, chăm sóc da, ăn sáng", "Khởi đầu ngày mới", TaskCategory.BREAKFAST),
        ScheduleItem("wed_2", Calendar.WEDNESDAY, dayName, 7, 0, 8, 0, "Di chuyển, nghe TOEIC", "Luyện nghe trên đường", TaskCategory.COMMUTE),
        ScheduleItem("wed_3", Calendar.WEDNESDAY, dayName, 8, 0, 12, 0, "Thực tập", "Tập trung công việc thực tập", TaskCategory.INTERNSHIP),
        ScheduleItem("wed_4", Calendar.WEDNESDAY, dayName, 12, 0, 13, 0, "Ăn trưa, ngủ trưa 20 phút", "Nghỉ ngơi tái tạo năng lượng", TaskCategory.LUNCH),
        ScheduleItem("wed_5", Calendar.WEDNESDAY, dayName, 13, 0, 17, 0, "Thực tập", "Hoàn tất công việc", TaskCategory.INTERNSHIP),
        ScheduleItem("wed_6", Calendar.WEDNESDAY, dayName, 17, 0, 18, 30, "Về, từ vựng TOEIC", "Học từ vựng mới", TaskCategory.TOEIC),
        ScheduleItem("wed_7", Calendar.WEDNESDAY, dayName, 18, 30, 19, 0, "Ăn tối nhẹ", "Bữa tối nhẹ trước khi học và đá bóng", TaskCategory.DINNER),
        ScheduleItem("wed_8", Calendar.WEDNESDAY, dayName, 19, 0, 20, 15, "Luận văn", "Tiếp tục đề tài luận văn", TaskCategory.THESIS),
        ScheduleItem("wed_9", Calendar.WEDNESDAY, dayName, 20, 15, 20, 30, "Chuẩn bị đồ đá bóng", "Soạn trang phục, giầy tất", TaskCategory.FOOTBALL),
        ScheduleItem("wed_10", Calendar.WEDNESDAY, dayName, 20, 30, 21, 0, "Di chuyển đến sân", "Đi đến sân bóng đá", TaskCategory.COMMUTE),
        ScheduleItem("wed_11", Calendar.WEDNESDAY, dayName, 21, 0, 23, 30, "Đá bóng", "Thi đấu năng động", TaskCategory.FOOTBALL),
        ScheduleItem("wed_12", Calendar.WEDNESDAY, dayName, 23, 30, 0, 0, "Di chuyển về", "Về nhà an toàn", TaskCategory.COMMUTE)
    )

    // Thứ 5: 00:00 & 00:30 thuộc đêm Thứ 4 sang Thứ 5; Buổi B thân dưới
    private fun getThursdaySchedule(dayName: String): List<ScheduleItem> = listOf(
        ScheduleItem("thu_0a", Calendar.THURSDAY, dayName, 0, 0, 0, 30, "Tắm, chăm sóc da, sữa chua", "Hồi phục sau trận đá bóng đêm T4", TaskCategory.SKINCARE, crossMidnightNote = "Đêm T4 sang T5"),
        ScheduleItem("thu_0b", Calendar.THURSDAY, dayName, 0, 30, 6, 15, "Ngủ", "Giấc ngủ sâu", TaskCategory.SLEEP, crossMidnightNote = "Đêm T4 sang T5"),
        ScheduleItem("thu_1", Calendar.THURSDAY, dayName, 6, 15, 7, 0, "Dậy, chăm sóc da, ăn sáng", "Chào ngày mới", TaskCategory.BREAKFAST),
        ScheduleItem("thu_2", Calendar.THURSDAY, dayName, 7, 0, 8, 0, "Di chuyển, nghe TOEIC", "Luyện nghe thụ động", TaskCategory.COMMUTE),
        ScheduleItem("thu_3", Calendar.THURSDAY, dayName, 8, 0, 12, 0, "Thực tập", "Công việc thực tập", TaskCategory.INTERNSHIP),
        ScheduleItem("thu_4", Calendar.THURSDAY, dayName, 12, 0, 13, 0, "Ăn trưa", "Bữa trưa ngon miệng", TaskCategory.LUNCH),
        ScheduleItem("thu_5", Calendar.THURSDAY, dayName, 13, 0, 17, 0, "Thực tập", "Nhiệm vụ công việc", TaskCategory.INTERNSHIP),
        ScheduleItem("thu_6", Calendar.THURSDAY, dayName, 17, 0, 18, 30, "Về, từ vựng TOEIC", "Ôn tập từ vựng", TaskCategory.TOEIC),
        ScheduleItem("thu_7", Calendar.THURSDAY, dayName, 18, 30, 19, 15, "Ăn tối", "Bữa tối ấm cúng", TaskCategory.DINNER),
        ScheduleItem("thu_8", Calendar.THURSDAY, dayName, 19, 15, 21, 15, "Luận văn", "Nghiên cứu tài liệu luận văn", TaskCategory.THESIS),
        ScheduleItem("thu_9", Calendar.THURSDAY, dayName, 21, 15, 22, 0, "Tập sức mạnh (Buổi B thân dưới)", "Chân, mông, đùi, bụng", TaskCategory.STRENGTH_TRAINING),
        ScheduleItem("thu_10", Calendar.THURSDAY, dayName, 22, 0, 22, 30, "Tắm, chăm sóc da", "Thư giãn cơ thể, dưỡng da", TaskCategory.SKINCARE),
        ScheduleItem("thu_11", Calendar.THURSDAY, dayName, 22, 30, 22, 45, "Thư giãn", "Nghỉ ngơi chuẩn bị ngủ", TaskCategory.RELAX),
        ScheduleItem("thu_12", Calendar.THURSDAY, dayName, 22, 45, 6, 15, "Ngủ", "Giấc ngủ ngon", TaskCategory.SLEEP)
    )

    // Thứ 6 (Tương tự Thứ 2, 4)
    private fun getFridaySchedule(dayName: String): List<ScheduleItem> = listOf(
        ScheduleItem("fri_1", Calendar.FRIDAY, dayName, 6, 15, 7, 0, "Dậy, chăm sóc da, ăn sáng", "Khởi đầu ngày làm việc cuối tuần", TaskCategory.BREAKFAST),
        ScheduleItem("fri_2", Calendar.FRIDAY, dayName, 7, 0, 8, 0, "Di chuyển, nghe TOEIC", "Nghe podcast / TOEIC", TaskCategory.COMMUTE),
        ScheduleItem("fri_3", Calendar.FRIDAY, dayName, 8, 0, 12, 0, "Thực tập", "Tổng kết tuần làm việc", TaskCategory.INTERNSHIP),
        ScheduleItem("fri_4", Calendar.FRIDAY, dayName, 12, 0, 13, 0, "Ăn trưa, ngủ trưa 20 phút", "Nghỉ trưa tái tạo sức", TaskCategory.LUNCH),
        ScheduleItem("fri_5", Calendar.FRIDAY, dayName, 13, 0, 17, 0, "Thực tập", "Bàn giao báo cáo tuần", TaskCategory.INTERNSHIP),
        ScheduleItem("fri_6", Calendar.FRIDAY, dayName, 17, 0, 18, 30, "Về, từ vựng TOEIC", "Ôn từ vựng trong tuần", TaskCategory.TOEIC),
        ScheduleItem("fri_7", Calendar.FRIDAY, dayName, 18, 30, 19, 0, "Ăn tối nhẹ", "Ăn nhẹ", TaskCategory.DINNER),
        ScheduleItem("fri_8", Calendar.FRIDAY, dayName, 19, 0, 20, 15, "Luận văn", "Tiến độ luận văn tuần này", TaskCategory.THESIS),
        ScheduleItem("fri_9", Calendar.FRIDAY, dayName, 20, 15, 20, 30, "Chuẩn bị đồ đá bóng", "Chuẩn bị giày và đồng phục", TaskCategory.FOOTBALL),
        ScheduleItem("fri_10", Calendar.FRIDAY, dayName, 20, 30, 21, 0, "Di chuyển đến sân", "Đi tới sân", TaskCategory.COMMUTE),
        ScheduleItem("fri_11", Calendar.FRIDAY, dayName, 21, 0, 23, 30, "Đá bóng", "Trận bóng đá tối thứ 6", TaskCategory.FOOTBALL),
        ScheduleItem("fri_12", Calendar.FRIDAY, dayName, 23, 30, 0, 0, "Di chuyển về", "Về nhà an toàn", TaskCategory.COMMUTE)
    )

    // Thứ 7: 00:00 & 00:30 thuộc đêm Thứ 6 sang Thứ 7
    private fun getSaturdaySchedule(dayName: String): List<ScheduleItem> = listOf(
        ScheduleItem("sat_0a", Calendar.SATURDAY, dayName, 0, 0, 0, 30, "Tắm, chăm sóc da, sữa chua", "Hồi phục sau trận đá bóng đêm T6", TaskCategory.SKINCARE, crossMidnightNote = "Đêm T6 sang T7"),
        ScheduleItem("sat_0b", Calendar.SATURDAY, dayName, 0, 30, 8, 0, "Ngủ", "Ngủ ngon giấc đến 8h", TaskCategory.SLEEP, crossMidnightNote = "Đêm T6 sang T7"),
        ScheduleItem("sat_1", Calendar.SATURDAY, dayName, 8, 0, 9, 0, "Dậy, ăn sáng", "Thức dậy thong thả, ăn sáng dinh dưỡng", TaskCategory.BREAKFAST),
        ScheduleItem("sat_2", Calendar.SATURDAY, dayName, 9, 0, 12, 0, "Luận văn", "Tập trung viết luận văn buổi sáng", TaskCategory.THESIS),
        ScheduleItem("sat_3", Calendar.SATURDAY, dayName, 12, 0, 13, 30, "Ăn trưa, nghỉ", "Ăn trưa và nghỉ ngơi 1.5 tiếng", TaskCategory.LUNCH),
        ScheduleItem("sat_4", Calendar.SATURDAY, dayName, 13, 30, 17, 30, "Luận văn", "Phiên luận văn chuyên sâu buổi chiều", TaskCategory.THESIS),
        ScheduleItem("sat_5", Calendar.SATURDAY, dayName, 17, 30, 19, 0, "Nghỉ, ăn tối", "Thư giãn và thưởng thức bữa tối", TaskCategory.DINNER),
        ScheduleItem("sat_6", Calendar.SATURDAY, dayName, 19, 0, 20, 30, "TOEIC", "Luyện giải đề / ngữ pháp TOEIC", TaskCategory.TOEIC),
        ScheduleItem("sat_7", Calendar.SATURDAY, dayName, 20, 30, 22, 30, "Tự do", "Giải trí, xem phim hoặc dạo phố", TaskCategory.RELAX),
        ScheduleItem("sat_8", Calendar.SATURDAY, dayName, 22, 30, 7, 0, "Ngủ", "Nghỉ ngơi dưỡng sức", TaskCategory.SLEEP)
    )

    // Chủ nhật
    private fun getSundaySchedule(dayName: String): List<ScheduleItem> = listOf(
        ScheduleItem("sun_1", Calendar.SUNDAY, dayName, 7, 0, 8, 0, "Dậy, chăm sóc da, đi bộ nhẹ", "Không khí trong lành sáng Chủ nhật", TaskCategory.SKINCARE),
        ScheduleItem("sun_2", Calendar.SUNDAY, dayName, 8, 0, 8, 30, "Ăn sáng", "Bữa sáng nhẹ nhàng", TaskCategory.BREAKFAST),
        ScheduleItem("sun_3", Calendar.SUNDAY, dayName, 8, 30, 12, 0, "Luận văn", "Hoàn thiện các mục luận văn", TaskCategory.THESIS),
        ScheduleItem("sun_4", Calendar.SUNDAY, dayName, 12, 0, 13, 30, "Ăn trưa", "Bữa trưa Chủ nhật", TaskCategory.LUNCH),
        ScheduleItem("sun_5", Calendar.SUNDAY, dayName, 13, 30, 17, 0, "TOEIC làm đề bấm giờ", "Thi thử full test TOEIC đúng thời gian", TaskCategory.TOEIC),
        ScheduleItem("sun_6", Calendar.SUNDAY, dayName, 17, 0, 18, 0, "Tập Buổi C toàn thân", "Full body workout", TaskCategory.STRENGTH_TRAINING),
        ScheduleItem("sun_7", Calendar.SUNDAY, dayName, 18, 0, 19, 30, "Ăn tối", "Bữa tối ấm cúng cùng gia đình / bạn bè", TaskCategory.DINNER),
        ScheduleItem("sun_8", Calendar.SUNDAY, dayName, 19, 30, 20, 15, "Tổng kết tuần", "Đánh giá tuần qua và lên kế hoạch tuần mới", TaskCategory.THESIS),
        ScheduleItem("sun_9", Calendar.SUNDAY, dayName, 20, 15, 22, 30, "Từ vựng TOEIC", "Ôn tập toàn bộ từ vựng trong tuần", TaskCategory.TOEIC),
        ScheduleItem("sun_10", Calendar.SUNDAY, dayName, 22, 30, 6, 15, "Ngủ", "Ngủ sớm để sáng mai T2 bắt đầu tuần mới", TaskCategory.SLEEP)
    )

    // Find current ongoing task and next upcoming task for a given timestamp
    fun getCurrentAndNextTask(calendar: Calendar = Calendar.getInstance()): Pair<ScheduleItem?, ScheduleItem?> {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        val todayItems = getItemsForDay(dayOfWeek)

        var currentItem: ScheduleItem? = null
        var nextItem: ScheduleItem? = null

        for (i in todayItems.indices) {
            val item = todayItems[i]
            val startMins = item.startMinutesOfDay
            val endMins = item.endMinutesOfDay

            if (currentMinutes in startMins until endMins) {
                currentItem = item
                nextItem = if (i + 1 < todayItems.size) {
                    todayItems[i + 1]
                } else {
                    // Next day's first item
                    val nextDay = if (dayOfWeek == Calendar.SATURDAY) Calendar.SUNDAY else if (dayOfWeek == Calendar.SUNDAY) Calendar.MONDAY else dayOfWeek + 1
                    getItemsForDay(nextDay).firstOrNull()
                }
                break
            } else if (currentMinutes < startMins) {
                if (nextItem == null) {
                    nextItem = item
                }
            }
        }

        // If no ongoing item matched in today's items (e.g. gap between events), pick next
        if (currentItem == null && nextItem == null && todayItems.isNotEmpty()) {
            val nextDay = if (dayOfWeek == Calendar.SATURDAY) Calendar.SUNDAY else if (dayOfWeek == Calendar.SUNDAY) Calendar.MONDAY else dayOfWeek + 1
            nextItem = getItemsForDay(nextDay).firstOrNull()
        }

        return Pair(currentItem, nextItem)
    }

    fun findItemById(id: String): ScheduleItem? {
        return getAllItems().firstOrNull { it.id == id }
    }
}
