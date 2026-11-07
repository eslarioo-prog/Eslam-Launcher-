package com.example

import com.example.model.AppCategory
import com.example.model.AppModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testAppFiltering_bySearchQuery() {
    val sampleApps = listOf(
      AppModel(packageName = "com.google.android.dialer", label = "Phone", category = AppCategory.COMMUNICATION),
      AppModel(packageName = "com.android.chrome", label = "Chrome", category = AppCategory.COMMUNICATION),
      AppModel(packageName = "com.google.android.GoogleCamera", label = "Camera", category = AppCategory.MEDIA)
    )

    val query = "cam"
    val filtered = sampleApps.filter { it.label.contains(query, ignoreCase = true) }

    assertEquals(1, filtered.size)
    assertEquals("Camera", filtered[0].label)
  }

  @Test
  fun testAppFiltering_byCategory() {
    val sampleApps = listOf(
      AppModel(packageName = "com.google.android.dialer", label = "Phone", category = AppCategory.COMMUNICATION),
      AppModel(packageName = "com.android.chrome", label = "Chrome", category = AppCategory.COMMUNICATION),
      AppModel(packageName = "com.google.android.GoogleCamera", label = "Camera", category = AppCategory.MEDIA)
    )

    val commApps = sampleApps.filter { it.category == AppCategory.COMMUNICATION }
    assertEquals(2, commApps.size)
  }
}
