package com.example

import com.example.data.ScheduleRepository
import com.example.model.AppSettings
import com.example.model.OngoingDisplayMode
import com.example.model.OngoingUpdateFrequency
import com.example.model.TaskCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

  @Test
  fun testDefaultOngoingNotificationSettings() {
    val defaultSettings = AppSettings()
    // Requirement 1: Default must be false (tắt)
    assertFalse("Ongoing notification should default to off", defaultSettings.ongoingNotificationEnabled)
    assertEquals(OngoingDisplayMode.ALWAYS, defaultSettings.ongoingDisplayMode)
    assertEquals(OngoingUpdateFrequency.EVERY_1_MIN, defaultSettings.ongoingUpdateFrequency)
    assertEquals(15, defaultSettings.ongoingBeforeEndMinutes)
    assertEquals(15, defaultSettings.ongoingBeforeNextMinutes)
  }

  @Test
  fun testOngoingCategoryFiltering() {
    val customSettings = AppSettings(
      ongoingNotificationEnabled = true,
      ongoingIncludedCategories = setOf(TaskCategory.THESIS.name, TaskCategory.TOEIC.name)
    )
    assertTrue(customSettings.isCategoryIncludedInOngoing(TaskCategory.THESIS.name))
    assertTrue(customSettings.isCategoryIncludedInOngoing(TaskCategory.TOEIC.name))
    assertFalse(customSettings.isCategoryIncludedInOngoing(TaskCategory.INTERNSHIP.name))
    assertFalse(customSettings.isCategoryIncludedInOngoing(TaskCategory.SLEEP.name))
  }
}
