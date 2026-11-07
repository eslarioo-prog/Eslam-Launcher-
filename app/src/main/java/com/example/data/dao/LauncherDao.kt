package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.AppItemEntity
import com.example.data.entity.FolderEntity
import com.example.data.entity.LauncherSettingEntity
import com.example.data.entity.WidgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherDao {

    // App Items
    @Query("SELECT * FROM home_app_items WHERE isHidden = 0 ORDER BY isDock DESC, pageIndex ASC, cellY ASC, cellX ASC")
    fun getVisibleAppItems(): Flow<List<AppItemEntity>>

    @Query("SELECT * FROM home_app_items ORDER BY id ASC")
    fun getAllAppItems(): Flow<List<AppItemEntity>>

    @Query("SELECT * FROM home_app_items WHERE isHidden = 1")
    fun getHiddenAppItems(): Flow<List<AppItemEntity>>

    @Query("SELECT * FROM home_app_items WHERE folderId = :folderId")
    fun getAppsInFolder(folderId: Long): Flow<List<AppItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppItem(item: AppItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppItems(items: List<AppItemEntity>)

    @Update
    suspend fun updateAppItem(item: AppItemEntity)

    @Query("DELETE FROM home_app_items WHERE id = :id")
    suspend fun deleteAppItemById(id: Long)

    @Query("DELETE FROM home_app_items WHERE packageName = :packageName AND isDock = 0 AND folderId IS NULL")
    suspend fun removeAppFromDesktop(packageName: String)

    @Query("UPDATE home_app_items SET launchCount = launchCount + 1, lastLaunchedTimestamp = :timestamp WHERE packageName = :packageName")
    suspend fun recordAppLaunch(packageName: String, timestamp: Long)

    @Query("UPDATE home_app_items SET isHidden = :hidden WHERE packageName = :packageName")
    suspend fun setAppHidden(packageName: String, hidden: Boolean)

    @Query("UPDATE home_app_items SET customLabel = :newLabel WHERE packageName = :packageName")
    suspend fun updateAppCustomLabel(packageName: String, newLabel: String?)

    @Query("UPDATE home_app_items SET isDock = 1, cellX = :slotIndex, cellY = 0, pageIndex = -1, folderId = NULL WHERE packageName = :packageName")
    suspend fun updateAppToDockSlot(packageName: String, slotIndex: Int): Int

    @Query("UPDATE home_app_items SET isDock = 0, pageIndex = 0, cellX = 0, cellY = 1 WHERE packageName = :packageName AND isDock = 1")
    suspend fun unpinFromDock(packageName: String)

    @Query("UPDATE home_app_items SET cellX = :newSlot WHERE packageName = :packageName AND isDock = 1")
    suspend fun updateDockSlot(packageName: String, newSlot: Int)

    // Folders
    @Query("SELECT * FROM home_folders ORDER BY pageIndex ASC, cellY ASC, cellX ASC")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity): Long

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Query("DELETE FROM home_folders WHERE id = :id")
    suspend fun deleteFolderById(id: Long)

    @Query("UPDATE home_app_items SET folderId = NULL WHERE folderId = :folderId")
    suspend fun releaseAppsFromFolder(folderId: Long)

    @Query("UPDATE home_app_items SET folderId = :folderId WHERE packageName = :packageName")
    suspend fun setAppFolder(packageName: String, folderId: Long?)

    @Query("UPDATE home_folders SET cellX = :newSlot WHERE id = :folderId AND isDock = 1")
    suspend fun updateDockFolderSlot(folderId: Long, newSlot: Int)

    // Widgets
    @Query("SELECT * FROM home_widgets ORDER BY pageIndex ASC, cellY ASC, cellX ASC")
    fun getAllWidgets(): Flow<List<WidgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: WidgetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgets(widgets: List<WidgetEntity>)

    @Update
    suspend fun updateWidget(widget: WidgetEntity)

    @Query("DELETE FROM home_widgets WHERE id = :id")
    suspend fun deleteWidgetById(id: Long)

    // Settings
    @Query("SELECT * FROM launcher_settings")
    fun getAllSettings(): Flow<List<LauncherSettingEntity>>

    @Query("SELECT value FROM launcher_settings WHERE `key` = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: LauncherSettingEntity)

    @Query("DELETE FROM home_app_items")
    suspend fun clearApps()

    @Query("DELETE FROM home_folders")
    suspend fun clearFolders()

    @Query("DELETE FROM home_widgets")
    suspend fun clearWidgets()
}
