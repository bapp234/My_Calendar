package com.example

import com.example.data.ScheduleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {

  @Test
  fun testScheduleItemsNotEmpty() {
    val allItems = ScheduleRepository.getAllItems()
    assertTrue("Schedule items should not be empty", allItems.isNotEmpty())
  }

  @Test
  fun testMondayScheduleCount() {
    val mondayItems = ScheduleRepository.getItemsForDay(Calendar.MONDAY)
    assertEquals(12, mondayItems.size)
  }

  @Test
  fun testTuesdayMidnightTransition() {
    val tuesdayItems = ScheduleRepository.getItemsForDay(Calendar.TUESDAY)
    assertNotNull(tuesdayItems)
    // Tuesday 00:00 is "Tắm, chăm sóc da, sữa chua" from Monday night
    val midnightItem = tuesdayItems.firstOrNull { it.startHour == 0 && it.startMinute == 0 }
    assertNotNull(midnightItem)
    assertEquals("Tắm, chăm sóc da, sữa chua", midnightItem?.title)
  }
}
