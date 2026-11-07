package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.LauncherDatabase
import com.example.data.repository.LauncherRepository
import com.example.model.AppCategory
import com.example.model.AppModel
import com.example.model.LauncherConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: LauncherDatabase
    private lateinit var repository: LauncherRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LauncherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = LauncherRepository(context, database.launcherDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Pixel Launcher", appName)
    }

    @Test
    fun testDefaultLayoutInitialization() = runBlocking {
        repository.initializeDefaultLayoutIfNeeded()
        val allApps = repository.allAppsFlow.first()
        assertTrue(allApps.isNotEmpty())

        val dockApps = repository.dockAppsFlow.first()
        assertEquals(5, dockApps.size)

        val settings = repository.settingsFlow.first()
        assertEquals("SQUIRCLE", settings.iconShape)
        assertEquals("PILL_BLUR", settings.dockStyle)
        assertEquals("GOOGLE", settings.searchEngine)
        assertEquals("NOTIFICATIONS", settings.swipeDownGesture)
        assertTrue(settings.doubleTapToSleep)
    }

    @Test
    fun testPinAndUnpinDockSlot() = runBlocking {
        repository.initializeDefaultLayoutIfNeeded()
        val app = AppModel(
            packageName = "com.test.sample",
            label = "Test App",
            category = AppCategory.SOCIAL
        )

        repository.pinAppToDock(app, targetSlot = 2)
        val dockApps = repository.dockAppsFlow.first()
        val pinned = dockApps.find { it.cellX == 2 }
        assertNotNull(pinned)
        assertEquals("com.test.sample", pinned?.packageName)

        repository.unpinAppFromDock("com.test.sample")
        val updatedDock = repository.dockAppsFlow.first()
        assertTrue(updatedDock.none { it.packageName == "com.test.sample" })
    }

    @Test
    fun testDockSlotReordering() = runBlocking {
        repository.initializeDefaultLayoutIfNeeded()
        val initialDock = repository.dockAppsFlow.first()
        val slot0App = initialDock.find { it.cellX == 0 }?.packageName
        val slot1App = initialDock.find { it.cellX == 1 }?.packageName

        repository.reorderDockSlots(0, 1)
        val reorderedDock = repository.dockAppsFlow.first()

        assertEquals(slot0App, reorderedDock.find { it.cellX == 1 }?.packageName)
        assertEquals(slot1App, reorderedDock.find { it.cellX == 0 }?.packageName)
    }

    @Test
    fun testSettingsUpdates() = runBlocking {
        repository.initializeDefaultLayoutIfNeeded()
        repository.setDockStyle("SOLID_CARD")
        repository.setSearchEngine("GEMINI_LIVE")
        repository.setSwipeDownGesture("APP_DRAWER")
        repository.setDoubleTapToSleep(false)

        val updatedSettings = repository.settingsFlow.first()
        assertEquals("SOLID_CARD", updatedSettings.dockStyle)
        assertEquals("GEMINI_LIVE", updatedSettings.searchEngine)
        assertEquals("APP_DRAWER", updatedSettings.swipeDownGesture)
        assertEquals(false, updatedSettings.doubleTapToSleep)
    }
}
