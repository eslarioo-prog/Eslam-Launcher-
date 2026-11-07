package com.example.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.repository.LauncherRepository
import com.example.model.AppCategory
import com.example.model.AppModel
import com.example.model.AppShortcut
import com.example.model.DiscoverNewsItem
import com.example.model.FolderModel
import com.example.model.LauncherConfig
import com.example.model.PixelWeatherData
import com.example.model.WidgetModel
import com.example.model.WidgetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class LauncherSheet {
    object Settings : LauncherSheet()
    object WallpaperAndStyle : LauncherSheet()
    object WidgetPicker : LauncherSheet()
    object WeatherForecast : LauncherSheet()
    object QuickSettingsShade : LauncherSheet()
    object UniversalSearch : LauncherSheet()
    data class AppInfoDialog(val app: AppModel) : LauncherSheet()
    data class RenameAppDialog(val app: AppModel) : LauncherSheet()
}

class LauncherViewModel(
    private val repository: LauncherRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initializeDefaultLayoutIfNeeded()
        }
    }

    val launcherConfig: StateFlow<LauncherConfig> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LauncherConfig()
        )

    val desktopApps: StateFlow<List<AppModel>> = repository.desktopAppsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dockApps: StateFlow<List<AppModel>> = repository.dockAppsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allApps: StateFlow<List<AppModel>> = repository.allAppsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val hiddenApps: StateFlow<List<AppModel>> = repository.hiddenAppsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val folders: StateFlow<List<FolderModel>> = repository.foldersFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val widgets: StateFlow<List<WidgetModel>> = repository.widgetsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val weatherData: StateFlow<PixelWeatherData> = MutableStateFlow(repository.getWeatherData())
    val discoverNews: StateFlow<List<DiscoverNewsItem>> = MutableStateFlow(repository.getGoogleDiscoverFeed())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AppCategory.ALL)
    val selectedCategory: StateFlow<AppCategory> = _selectedCategory.asStateFlow()

    private val _activeFolder = MutableStateFlow<FolderModel?>(null)
    val activeFolder: StateFlow<FolderModel?> = _activeFolder.asStateFlow()

    private val _selectedAppForContext = MutableStateFlow<AppModel?>(null)
    val selectedAppForContext: StateFlow<AppModel?> = _selectedAppForContext.asStateFlow()

    private val _activeSheet = MutableStateFlow<LauncherSheet?>(null)
    val activeSheet: StateFlow<LauncherSheet?> = _activeSheet.asStateFlow()

    private val _isDrawerOpen = MutableStateFlow(false)
    val isDrawerOpen: StateFlow<Boolean> = _isDrawerOpen.asStateFlow()

    // Filtered drawer apps combining search query and category filter
    val filteredDrawerApps: StateFlow<List<AppModel>> = combine(
        repository.visibleAppsFlow,
        _searchQuery,
        _selectedCategory
    ) { apps, query, category ->
        var list = apps
        if (category != AppCategory.ALL) {
            list = when (category) {
                AppCategory.SUGGESTED -> list.sortedByDescending { it.launchCount }.take(10)
                else -> list.filter { it.category == category }
            }
        }
        if (query.isNotBlank()) {
            list = list.filter {
                it.label.contains(query, ignoreCase = true) ||
                        it.packageName.contains(query, ignoreCase = true)
            }
        }
        list.sortedBy { it.label.lowercase() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: AppCategory) {
        _selectedCategory.value = category
    }

    fun openDrawer() {
        _isDrawerOpen.value = true
    }

    fun closeDrawer() {
        _isDrawerOpen.value = false
        _searchQuery.value = ""
        _selectedCategory.value = AppCategory.ALL
    }

    fun openFolder(folder: FolderModel) {
        _activeFolder.value = folder
    }

    fun closeFolder() {
        _activeFolder.value = null
    }

    fun openContextMenu(app: AppModel) {
        _selectedAppForContext.value = app
    }

    fun closeContextMenu() {
        _selectedAppForContext.value = null
    }

    fun openSheet(sheet: LauncherSheet) {
        _activeSheet.value = sheet
    }

    fun closeSheet() {
        _activeSheet.value = null
    }

    fun launchApp(context: Context, app: AppModel) {
        viewModelScope.launch {
            repository.recordAppLaunch(app.packageName)
        }
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
        if (launchIntent != null) {
            try {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                return
            } catch (e: Exception) {
                // Fallback to simulated launch feedback
            }
        }

        // Dedicated built-in actions for standard Google/Pixel suite
        when (app.packageName) {
            "com.google.android.dialer" -> {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                if (dialIntent.resolveActivity(pm) != null) {
                    context.startActivity(dialIntent)
                } else {
                    Toast.makeText(context, "Opening Pixel Phone dialer...", Toast.LENGTH_SHORT).show()
                }
            }
            "com.google.android.apps.messaging" -> {
                val smsIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_MESSAGING)
                }
                if (smsIntent.resolveActivity(pm) != null) {
                    context.startActivity(smsIntent)
                } else {
                    Toast.makeText(context, "Opening Pixel Messages...", Toast.LENGTH_SHORT).show()
                }
            }
            "com.android.chrome" -> {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                try {
                    context.startActivity(webIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Opening Chrome Browser...", Toast.LENGTH_SHORT).show()
                }
            }
            "com.google.android.GoogleCamera" -> {
                val camIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                if (camIntent.resolveActivity(pm) != null) {
                    context.startActivity(camIntent)
                } else {
                    Toast.makeText(context, "Opening Pixel Camera...", Toast.LENGTH_SHORT).show()
                }
            }
            "com.android.settings" -> {
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                try {
                    context.startActivity(settingsIntent)
                } catch (e: Exception) {
                    openSheet(LauncherSheet.Settings)
                }
            }
            "com.google.android.apps.weather" -> {
                openSheet(LauncherSheet.WeatherForecast)
            }
            "com.google.android.googlequicksearchbox" -> {
                openSheet(LauncherSheet.UniversalSearch)
            }
            else -> {
                Toast.makeText(context, "Launching ${app.label}...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchShortcut(context: Context, app: AppModel, shortcut: AppShortcut) {
        closeContextMenu()
        Toast.makeText(context, "${app.label}: ${shortcut.label}", Toast.LENGTH_SHORT).show()
    }

    fun pinAppToHome(app: AppModel) {
        viewModelScope.launch {
            // Find first available slot on Page 0 or Page 1
            val currentDesktop = desktopApps.value
            val nextY = (currentDesktop.maxOfOrNull { it.cellY } ?: 0) + 1
            repository.pinAppToDesktop(app, 0, 0, minOf(nextY, 4))
            closeContextMenu()
        }
    }

    fun unpinAppFromHome(app: AppModel) {
        viewModelScope.launch {
            repository.unpinAppFromDesktop(app.packageName)
            closeContextMenu()
        }
    }

    fun pinAppToDock(app: AppModel, targetSlot: Int = -1) {
        viewModelScope.launch {
            val slot = if (targetSlot in 0..4) {
                targetSlot
            } else {
                val currentDock = repository.dockAppsFlow.first()
                val occupiedSlots = currentDock.map { it.cellX }.toSet()
                (0..4).firstOrNull { it !in occupiedSlots } ?: 4
            }
            repository.pinAppToDock(app, slot)
            closeContextMenu()
        }
    }

    fun unpinAppFromDock(app: AppModel) {
        viewModelScope.launch {
            repository.unpinAppFromDock(app.packageName)
            closeContextMenu()
        }
    }

    fun reorderDockSlots(fromSlot: Int, toSlot: Int) {
        viewModelScope.launch {
            repository.reorderDockSlots(fromSlot, toSlot)
        }
    }

    fun setAppHidden(app: AppModel, hidden: Boolean) {
        viewModelScope.launch {
            repository.setAppHidden(app.packageName, hidden)
            closeContextMenu()
        }
    }

    fun renameApp(app: AppModel, newName: String) {
        viewModelScope.launch {
            val labelToSave = if (newName.isBlank() || newName == app.defaultLabel) null else newName.trim()
            repository.renameApp(app.packageName, labelToSave)
            closeSheet()
            closeContextMenu()
        }
    }

    fun createFolder(title: String, packageNames: List<String>) {
        viewModelScope.launch {
            repository.createFolder(title, 0, 0, 3, packageNames)
        }
    }

    fun autoGroupAppsByCategory() {
        viewModelScope.launch {
            repository.autoGroupAppsByCategory()
        }
    }

    fun resetAppGrouping() {
        viewModelScope.launch {
            repository.resetAppGrouping()
        }
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            repository.deleteFolder(folderId)
            closeFolder()
        }
    }

    fun addWidget(type: WidgetType) {
        viewModelScope.launch {
            repository.addWidget(type, 1, 0, 0)
            closeSheet()
        }
    }

    fun removeWidget(widgetId: Long) {
        viewModelScope.launch {
            repository.removeWidget(widgetId)
        }
    }

    fun updateWallpaper(wallpaperId: String) {
        viewModelScope.launch {
            repository.updateSetting("wallpaper_id", wallpaperId)
            val extractedPalette = when (wallpaperId) {
                "meadow" -> "Meadow Mint"
                "sunset" -> "Coral Rose"
                "dynamic_aurora" -> "Bay Cyan"
                else -> "Obsidian Slate"
            }
            repository.updateSetting("palette_name", extractedPalette)
        }
    }

    fun suggestSmartFolderName(app1: AppModel, app2: AppModel): String {
        val cat1 = app1.category
        val cat2 = app2.category
        if (cat1 == cat2 && cat1 != AppCategory.ALL) {
            return cat1.title
        }
        val set = setOf(cat1, cat2)
        return when {
            AppCategory.SOCIAL in set || AppCategory.COMMUNICATION in set -> "Social & Chat"
            AppCategory.GAMES in set -> "Games & Play"
            AppCategory.MEDIA in set -> "Media & Entertainment"
            AppCategory.PRODUCTIVITY in set -> "Productivity & Work"
            AppCategory.GOOGLE in set -> "Google & Pixel"
            AppCategory.UTILITIES in set -> "Utilities & Tools"
            else -> "Smart Folder"
        }
    }

    fun updatePalette(paletteName: String) {
        viewModelScope.launch {
            repository.updateSetting("palette_name", paletteName)
        }
    }

    fun toggleThemedIcons(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSetting("themed_icons", enabled.toString())
        }
    }

    fun setIconShape(shape: String) {
        viewModelScope.launch {
            repository.updateSetting("icon_shape", shape)
        }
    }

    fun setDockStyle(style: String) {
        viewModelScope.launch {
            repository.setDockStyle(style)
        }
    }

    fun setDoubleTapToSleep(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDoubleTapToSleep(enabled)
        }
    }

    fun setSwipeDownGesture(action: String) {
        viewModelScope.launch {
            repository.setSwipeDownGesture(action)
        }
    }

    fun setSearchEngine(engine: String) {
        viewModelScope.launch {
            repository.setSearchEngine(engine)
        }
    }

    fun createDockFolder(slotIndex: Int, app1Package: String, app2Package: String, title: String = "Folder") {
        viewModelScope.launch {
            repository.createDockFolder(slotIndex, app1Package, app2Package, title)
        }
    }

    fun addAppToFolder(folderId: Long, packageName: String) {
        viewModelScope.launch {
            repository.addAppToFolder(folderId, packageName)
        }
    }

    fun setGridSize(rows: Int, cols: Int) {
        viewModelScope.launch {
            repository.updateSetting("grid_rows", rows.toString())
            repository.updateSetting("grid_cols", cols.toString())
        }
    }

    fun setDarkMode(mode: String) {
        viewModelScope.launch {
            repository.updateSetting("dark_mode", mode)
        }
    }

    fun toggleAtAGlanceOption(key: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSetting(key, enabled.toString())
        }
    }

    fun openAppInfo(context: Context, app: AppModel) {
        closeContextMenu()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", app.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            openSheet(LauncherSheet.AppInfoDialog(app))
        }
    }
}

class LauncherViewModelFactory(
    private val repository: LauncherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LauncherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LauncherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
