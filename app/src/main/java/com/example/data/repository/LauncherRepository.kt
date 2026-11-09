package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.example.R
import com.example.data.dao.LauncherDao
import com.example.data.entity.AppItemEntity
import com.example.data.entity.FolderEntity
import com.example.data.entity.LauncherSettingEntity
import com.example.data.entity.WidgetEntity
import com.example.model.AppCategory
import com.example.model.AppModel
import com.example.model.AppShortcut
import com.example.model.DiscoverNewsItem
import com.example.model.FolderModel
import com.example.model.LauncherConfig
import com.example.model.PixelWeatherData
import com.example.model.WidgetModel
import com.example.model.WidgetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LauncherRepository(
    private val context: Context,
    private val launcherDao: LauncherDao
) {

    // Pixel default predefined core apps with rich metadata, shortcuts, and categories
    private val pixelPreinstalledApps = listOf(
        AppModel(
            packageName = "com.google.android.dialer",
            label = "Phone",
            defaultLabel = "Phone",
            systemIconKey = "phone",
            category = AppCategory.COMMUNICATION,
            isDock = true,
            isPinned = true,
            cellX = 0,
            cellY = 0,
            shortcuts = listOf(
                AppShortcut("fav_1", "Speed Dial: Mom", "person"),
                AppShortcut("fav_2", "Voicemail", "voicemail"),
                AppShortcut("fav_3", "New Contact", "person_add")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.messaging",
            label = "Messages",
            defaultLabel = "Messages",
            systemIconKey = "messages",
            category = AppCategory.COMMUNICATION,
            isDock = true,
            isPinned = true,
            cellX = 1,
            cellY = 0,
            shortcuts = listOf(
                AppShortcut("msg_1", "Start chat", "chat"),
                AppShortcut("msg_2", "Alex Rivera", "account_circle"),
                AppShortcut("msg_3", "Pixel Beta Team", "group")
            )
        ),
        AppModel(
            packageName = "com.android.chrome",
            label = "Chrome",
            defaultLabel = "Chrome",
            systemIconKey = "chrome",
            category = AppCategory.COMMUNICATION,
            isDock = true,
            isPinned = true,
            cellX = 2,
            cellY = 0,
            shortcuts = listOf(
                AppShortcut("chr_1", "New Incognito tab", "security"),
                AppShortcut("chr_2", "New tab", "add_circle"),
                AppShortcut("chr_3", "Bookmarks", "bookmark")
            )
        ),
        AppModel(
            packageName = "com.google.android.GoogleCamera",
            label = "Camera",
            defaultLabel = "Camera",
            systemIconKey = "camera",
            category = AppCategory.MEDIA,
            isDock = true,
            isPinned = true,
            cellX = 3,
            cellY = 0,
            shortcuts = listOf(
                AppShortcut("cam_1", "Take selfie", "face"),
                AppShortcut("cam_2", "Record video", "videocam"),
                AppShortcut("cam_3", "Night Sight", "dark_mode"),
                AppShortcut("cam_4", "Portrait mode", "portrait")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.photos",
            label = "Photos",
            defaultLabel = "Photos",
            systemIconKey = "photos",
            category = AppCategory.MEDIA,
            isDock = true,
            isPinned = true,
            cellX = 4,
            cellY = 0,
            shortcuts = listOf(
                AppShortcut("pho_1", "Magic Editor", "auto_fix_high"),
                AppShortcut("pho_2", "Favorites", "favorite"),
                AppShortcut("pho_3", "Free up space", "delete_sweep")
            )
        ),
        AppModel(
            packageName = "com.google.android.googlequicksearchbox",
            label = "Google",
            defaultLabel = "Google",
            systemIconKey = "google",
            category = AppCategory.GOOGLE,
            isPinned = true,
            pageIndex = 0,
            cellX = 0,
            cellY = 1,
            shortcuts = listOf(
                AppShortcut("goo_1", "Search Lens", "search"),
                AppShortcut("goo_2", "Voice search", "mic"),
                AppShortcut("goo_3", "Trending topics", "trending_up")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.bard",
            label = "Gemini",
            defaultLabel = "Gemini",
            systemIconKey = "gemini",
            category = AppCategory.GOOGLE,
            isPinned = true,
            pageIndex = 0,
            cellX = 1,
            cellY = 1,
            shortcuts = listOf(
                AppShortcut("gem_1", "Ask Gemini Live", "graphic_eq"),
                AppShortcut("gem_2", "Generate image", "image"),
                AppShortcut("gem_3", "Help me write", "edit")
            )
        ),
        AppModel(
            packageName = "com.google.android.gm",
            label = "Gmail",
            defaultLabel = "Gmail",
            systemIconKey = "gmail",
            category = AppCategory.COMMUNICATION,
            isPinned = true,
            pageIndex = 0,
            cellX = 2,
            cellY = 1,
            shortcuts = listOf(
                AppShortcut("gm_1", "Compose", "edit"),
                AppShortcut("gm_2", "Primary inbox", "inbox")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.maps",
            label = "Maps",
            defaultLabel = "Maps",
            systemIconKey = "maps",
            category = AppCategory.UTILITIES,
            isPinned = true,
            pageIndex = 0,
            cellX = 3,
            cellY = 1,
            shortcuts = listOf(
                AppShortcut("map_1", "Work commute", "work"),
                AppShortcut("map_2", "Home", "home"),
                AppShortcut("map_3", "Gas stations", "local_gas_station")
            )
        ),
        AppModel(
            packageName = "com.google.android.youtube",
            label = "YouTube",
            defaultLabel = "YouTube",
            systemIconKey = "youtube",
            category = AppCategory.MEDIA,
            isPinned = true,
            pageIndex = 0,
            cellX = 0,
            cellY = 2,
            shortcuts = listOf(
                AppShortcut("yt_1", "Shorts", "smart_display"),
                AppShortcut("yt_2", "Subscriptions", "subscriptions"),
                AppShortcut("yt_3", "Search", "search")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.weather",
            label = "Weather",
            defaultLabel = "Weather",
            systemIconKey = "weather",
            category = AppCategory.UTILITIES,
            isPinned = true,
            pageIndex = 0,
            cellX = 1,
            cellY = 2,
            shortcuts = listOf(
                AppShortcut("wth_1", "10-day forecast", "cloud"),
                AppShortcut("wth_2", "Precipitation radar", "umbrella")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.docs",
            label = "Drive",
            defaultLabel = "Drive",
            systemIconKey = "drive",
            category = AppCategory.PRODUCTIVITY,
            isPinned = true,
            pageIndex = 0,
            cellX = 2,
            cellY = 2,
            shortcuts = listOf(
                AppShortcut("drv_1", "Scan document", "document_scanner"),
                AppShortcut("drv_2", "Recent files", "schedule")
            )
        ),
        AppModel(
            packageName = "com.android.vending",
            label = "Play Store",
            defaultLabel = "Play Store",
            systemIconKey = "play_store",
            category = AppCategory.GOOGLE,
            isPinned = true,
            pageIndex = 0,
            cellX = 3,
            cellY = 2,
            shortcuts = listOf(
                AppShortcut("ps_1", "My apps & games", "apps"),
                AppShortcut("ps_2", "Check updates", "update")
            )
        ),
        AppModel(
            packageName = "com.google.android.deskclock",
            label = "Clock",
            defaultLabel = "Clock",
            systemIconKey = "clock",
            category = AppCategory.UTILITIES,
            isPinned = true,
            pageIndex = 0,
            cellX = 0,
            cellY = 3,
            shortcuts = listOf(
                AppShortcut("clk_1", "Set 15m timer", "timer"),
                AppShortcut("clk_2", "Stopwatch", "hourglass_top"),
                AppShortcut("clk_3", "New Alarm", "alarm")
            )
        ),
        AppModel(
            packageName = "com.google.android.calculator",
            label = "Calculator",
            defaultLabel = "Calculator",
            systemIconKey = "calculator",
            category = AppCategory.UTILITIES,
            isPinned = true,
            pageIndex = 0,
            cellX = 1,
            cellY = 3,
            shortcuts = listOf(
                AppShortcut("calc_1", "Unit converter", "straighten"),
                AppShortcut("calc_2", "Currency exchange", "currency_exchange")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.nbu.files",
            label = "Files",
            defaultLabel = "Files",
            systemIconKey = "files",
            category = AppCategory.UTILITIES,
            isPinned = true,
            pageIndex = 0,
            cellX = 2,
            cellY = 3,
            shortcuts = listOf(
                AppShortcut("fil_1", "Clean junk files", "cleaning_services"),
                AppShortcut("fil_2", "Downloads", "download")
            )
        ),
        AppModel(
            packageName = "com.android.settings",
            label = "Settings",
            defaultLabel = "Settings",
            systemIconKey = "settings",
            category = AppCategory.SYSTEM,
            isPinned = true,
            pageIndex = 0,
            cellX = 3,
            cellY = 3,
            shortcuts = listOf(
                AppShortcut("set_1", "Battery usage", "battery_charging_full"),
                AppShortcut("set_2", "Wallpaper & style", "palette"),
                AppShortcut("set_3", "Network & internet", "wifi")
            )
        ),
        AppModel(
            packageName = "com.google.android.keep",
            label = "Keep Notes",
            defaultLabel = "Keep Notes",
            systemIconKey = "notes",
            category = AppCategory.PRODUCTIVITY,
            pageIndex = 1,
            cellX = 0,
            cellY = 0,
            shortcuts = listOf(
                AppShortcut("kp_1", "New checklist", "checklist"),
                AppShortcut("kp_2", "Voice note", "mic")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.recorder",
            label = "Recorder",
            defaultLabel = "Recorder",
            systemIconKey = "recorder",
            category = AppCategory.UTILITIES,
            pageIndex = 1,
            cellX = 1,
            cellY = 0,
            shortcuts = listOf(
                AppShortcut("rec_1", "Quick record", "fiber_manual_record")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.pixelstudio",
            label = "Pixel Studio",
            defaultLabel = "Pixel Studio",
            systemIconKey = "studio",
            category = AppCategory.MEDIA,
            pageIndex = 1,
            cellX = 2,
            cellY = 0,
            shortcuts = listOf(
                AppShortcut("stu_1", "AI Sticker creator", "auto_awesome"),
                AppShortcut("stu_2", "Generative canvas", "brush")
            )
        ),
        AppModel(
            packageName = "com.google.android.apps.chromecast.app",
            label = "Google Home",
            defaultLabel = "Google Home",
            systemIconKey = "home",
            category = AppCategory.UTILITIES,
            pageIndex = 1,
            cellX = 3,
            cellY = 0
        ),
        AppModel(
            packageName = "com.google.android.apps.fitness",
            label = "Fitbit",
            defaultLabel = "Fitbit",
            systemIconKey = "fitness",
            category = AppCategory.UTILITIES,
            pageIndex = 1,
            cellX = 0,
            cellY = 1
        ),
        AppModel(
            packageName = "com.google.android.contacts",
            label = "Contacts",
            defaultLabel = "Contacts",
            systemIconKey = "contacts",
            category = AppCategory.COMMUNICATION,
            pageIndex = 1,
            cellX = 1,
            cellY = 1
        )
    )

    // Observable stream of all apps (installed + simulated Pixel apps with DB overrides)
    val allAppsFlow: Flow<List<AppModel>> = launcherDao.getAllAppItems().map { dbItems ->
        mergeAppsWithDatabase(dbItems)
    }.flowOn(Dispatchers.IO)

    // Visible apps on Desktop and Drawer
    val visibleAppsFlow: Flow<List<AppModel>> = allAppsFlow.map { apps ->
        apps.filterNot { it.isHidden }
    }

    val hiddenAppsFlow: Flow<List<AppModel>> = allAppsFlow.map { apps ->
        apps.filter { it.isHidden }
    }

    val desktopAppsFlow: Flow<List<AppModel>> = visibleAppsFlow.map { apps ->
        apps.filter { it.isPinned && !it.isDock && it.folderId == null }
    }

    val dockAppsFlow: Flow<List<AppModel>> = visibleAppsFlow.map { apps ->
        apps.filter { it.isDock && it.folderId == null }.sortedBy { it.cellX }
    }

    val foldersFlow: Flow<List<FolderModel>> = combine(
        launcherDao.getAllFolders(),
        visibleAppsFlow
    ) { folderEntities, apps ->
        folderEntities.map { entity ->
            val containedApps = apps.filter { it.folderId == entity.id }
            FolderModel(
                id = entity.id,
                title = entity.title,
                pageIndex = entity.pageIndex,
                cellX = entity.cellX,
                cellY = entity.cellY,
                isDock = entity.isDock,
                apps = containedApps,
                colorTag = entity.colorTag
            )
        }
    }.flowOn(Dispatchers.IO)

    val widgetsFlow: Flow<List<WidgetModel>> = launcherDao.getAllWidgets().map { entities ->
        entities.mapNotNull { entity ->
            val type = try {
                WidgetType.valueOf(entity.widgetType)
            } catch (e: Exception) {
                null
            }
            type?.let {
                WidgetModel(
                    id = entity.id,
                    type = it,
                    pageIndex = entity.pageIndex,
                    cellX = entity.cellX,
                    cellY = entity.cellY,
                    spanX = entity.spanX,
                    spanY = entity.spanY,
                    configData = entity.configData
                )
            }
        }
    }.flowOn(Dispatchers.IO)

    val settingsFlow: Flow<LauncherConfig> = launcherDao.getAllSettings().map { settingsList ->
        val map = settingsList.associate { it.key to it.value }
        LauncherConfig(
            wallpaperId = map["wallpaper_id"] ?: "obsidian",
            colorPaletteName = map["palette_name"] ?: "Obsidian Slate",
            useThemedIcons = map["themed_icons"]?.toBooleanStrictOrNull() ?: false,
            iconShape = map["icon_shape"] ?: "SQUIRCLE",
            dockStyle = map["dock_style"] ?: "PILL_BLUR",
            doubleTapToSleep = map["double_tap_to_sleep"]?.toBooleanStrictOrNull() ?: true,
            swipeDownGesture = map["swipe_down_gesture"] ?: "NOTIFICATIONS",
            searchEngine = map["search_engine"] ?: "GOOGLE",
            gridRows = map["grid_rows"]?.toIntOrNull() ?: 5,
            gridCols = map["grid_cols"]?.toIntOrNull() ?: 4,
            showDockSearch = map["dock_search"]?.toBooleanStrictOrNull() ?: true,
            showAtAGlanceWeather = map["aag_weather"]?.toBooleanStrictOrNull() ?: true,
            showAtAGlanceCalendar = map["aag_calendar"]?.toBooleanStrictOrNull() ?: true,
            showAtAGlanceBattery = map["aag_battery"]?.toBooleanStrictOrNull() ?: true,
            darkModePreference = map["dark_mode"] ?: "SYSTEM"
        )
    }.flowOn(Dispatchers.IO)

    suspend fun initializeDefaultLayoutIfNeeded() = withContext(Dispatchers.IO) {
        launcherDao.deleteWidgetsByType("BATTERY_STATUS")
        launcherDao.deleteWidgetsByType("MUSIC_PLAYER")
        launcherDao.deleteWidgetsByType("DIGITAL_WELLBEING")
        launcherDao.deleteWidgetsByType("PIXEL_STUDIO")
        val existingApps = launcherDao.getAllAppItems().first()
        if (existingApps.isEmpty()) {
            // Populate defaults
            val entities = pixelPreinstalledApps.map { app ->
                AppItemEntity(
                    packageName = app.packageName,
                    activityName = app.activityName,
                    customLabel = null,
                    defaultLabel = app.label,
                    pageIndex = app.pageIndex,
                    cellX = app.cellX,
                    cellY = app.cellY,
                    isDock = app.isDock,
                    folderId = null,
                    isHidden = false,
                    isPinned = app.isPinned,
                    iconKey = app.systemIconKey
                )
            }
            launcherDao.insertAppItems(entities)

            // Insert default widgets (At a Glance on Page 0)
            val defaultWidgets = listOf(
                WidgetEntity(
                    widgetType = WidgetType.AT_A_GLANCE.name,
                    pageIndex = 0,
                    cellX = 0,
                    cellY = 0,
                    spanX = 4,
                    spanY = 1
                )
            )
            launcherDao.insertWidgets(defaultWidgets)
            launcherDao.deleteWidgetsByType("BATTERY_STATUS")

            // Insert default settings
            launcherDao.setSetting(LauncherSettingEntity("wallpaper_id", "obsidian"))
            launcherDao.setSetting(LauncherSettingEntity("palette_name", "Obsidian Slate"))
            launcherDao.setSetting(LauncherSettingEntity("themed_icons", "false"))
            launcherDao.setSetting(LauncherSettingEntity("icon_shape", "SQUIRCLE"))
            launcherDao.setSetting(LauncherSettingEntity("grid_rows", "5"))
            launcherDao.setSetting(LauncherSettingEntity("grid_cols", "4"))
            launcherDao.setSetting(LauncherSettingEntity("dock_search", "true"))
            launcherDao.setSetting(LauncherSettingEntity("dock_style", "PILL_BLUR"))
            launcherDao.setSetting(LauncherSettingEntity("double_tap_to_sleep", "true"))
            launcherDao.setSetting(LauncherSettingEntity("swipe_down_gesture", "NOTIFICATIONS"))
            launcherDao.setSetting(LauncherSettingEntity("search_engine", "GOOGLE"))
            launcherDao.setSetting(LauncherSettingEntity("aag_weather", "true"))
            launcherDao.setSetting(LauncherSettingEntity("aag_calendar", "true"))
            launcherDao.setSetting(LauncherSettingEntity("aag_battery", "true"))
            launcherDao.setSetting(LauncherSettingEntity("dark_mode", "SYSTEM"))
        } else {
            // Ensure themed_icons is set to false for classic Pixel theme
            launcherDao.setSetting(LauncherSettingEntity("themed_icons", "false"))
        }
    }

    private var cachedInstalledApps: List<android.content.pm.ResolveInfo>? = null
    private val appLabelCache = java.util.concurrent.ConcurrentHashMap<String, String>()

    private fun getInstalledAppsList(): List<android.content.pm.ResolveInfo> {
        val cached = cachedInstalledApps
        if (cached != null) return cached
        val packageManager = context.packageManager
        val list = try {
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            packageManager.queryIntentActivities(mainIntent, 0)
        } catch (e: Exception) {
            emptyList()
        }
        cachedInstalledApps = list
        return list
    }

    private fun mergeAppsWithDatabase(dbItems: List<AppItemEntity>): List<AppModel> {
        val dbMap = dbItems.associateBy { it.packageName }
        val packageManager = context.packageManager
        val installedAppsList = getInstalledAppsList()

        val result = mutableListOf<AppModel>()
        val processedPackages = mutableSetOf<String>()

        // 1. Process Predefined Pixel Suite
        pixelPreinstalledApps.forEach { baseApp ->
            val dbItem = dbMap[baseApp.packageName]
            processedPackages.add(baseApp.packageName)
            result.add(
                baseApp.copy(
                    label = dbItem?.customLabel ?: baseApp.label,
                    defaultLabel = baseApp.defaultLabel,
                    pageIndex = dbItem?.pageIndex ?: baseApp.pageIndex,
                    cellX = dbItem?.cellX ?: baseApp.cellX,
                    cellY = dbItem?.cellY ?: baseApp.cellY,
                    isDock = dbItem?.isDock ?: baseApp.isDock,
                    folderId = dbItem?.folderId,
                    isHidden = dbItem?.isHidden ?: false,
                    isPinned = dbItem?.isPinned ?: baseApp.isPinned,
                    launchCount = dbItem?.launchCount ?: 0
                )
            )
        }

        // 2. Process real system installed packages that aren't in base list
        installedAppsList.forEach { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            if (pkg != context.packageName && !processedPackages.contains(pkg)) {
                processedPackages.add(pkg)
                val label = appLabelCache.getOrPut(pkg) {
                    try {
                        resolveInfo.loadLabel(packageManager).toString()
                    } catch (e: Exception) {
                        pkg
                    }
                }
                val dbItem = dbMap[pkg]
                val systemKey = guessSystemIconKey(pkg, label)
                val category = guessCategory(pkg, label)

                result.add(
                    AppModel(
                        packageName = pkg,
                        activityName = resolveInfo.activityInfo.name,
                        label = dbItem?.customLabel ?: label,
                        defaultLabel = label,
                        systemIconKey = systemKey,
                        category = category,
                        pageIndex = dbItem?.pageIndex ?: 1,
                        cellX = dbItem?.cellX ?: 0,
                        cellY = dbItem?.cellY ?: 0,
                        isDock = dbItem?.isDock ?: false,
                        folderId = dbItem?.folderId,
                        isHidden = dbItem?.isHidden ?: false,
                        isPinned = dbItem?.isPinned ?: false,
                        launchCount = dbItem?.launchCount ?: 0
                    )
                )
            }
        }

        // 3. Process remaining custom database items not in preinstalled or system packages
        dbItems.forEach { dbItem ->
            if (!processedPackages.contains(dbItem.packageName)) {
                processedPackages.add(dbItem.packageName)
                val label = dbItem.customLabel ?: dbItem.defaultLabel
                result.add(
                    AppModel(
                        packageName = dbItem.packageName,
                        activityName = dbItem.activityName ?: "",
                        label = label,
                        defaultLabel = dbItem.defaultLabel,
                        systemIconKey = dbItem.iconKey ?: "android",
                        category = guessCategory(dbItem.packageName, label),
                        pageIndex = dbItem.pageIndex,
                        cellX = dbItem.cellX,
                        cellY = dbItem.cellY,
                        isDock = dbItem.isDock,
                        folderId = dbItem.folderId,
                        isHidden = dbItem.isHidden,
                        isPinned = dbItem.isPinned,
                        launchCount = dbItem.launchCount
                    )
                )
            }
        }

        return result
    }

    private fun guessSystemIconKey(pkg: String, label: String): String {
        val lower = "$pkg $label".lowercase()
        return when {
            "camera" in lower -> "camera"
            "photo" in lower || "gallery" in lower -> "photos"
            "phone" in lower || "call" in lower || "dialer" in lower -> "phone"
            "message" in lower || "sms" in lower || "chat" in lower -> "messages"
            "browser" in lower || "chrome" in lower || "web" in lower -> "chrome"
            "music" in lower || "audio" in lower || "sound" in lower -> "music"
            "video" in lower || "movie" in lower || "youtube" in lower -> "youtube"
            "mail" in lower || "gmail" in lower || "email" in lower -> "gmail"
            "map" in lower || "nav" in lower || "gps" in lower -> "maps"
            "clock" in lower || "alarm" in lower || "time" in lower -> "clock"
            "calc" in lower -> "calculator"
            "file" in lower || "folder" in lower -> "files"
            "note" in lower || "keep" in lower || "memo" in lower -> "notes"
            "setting" in lower || "config" in lower -> "settings"
            "weather" in lower -> "weather"
            "contact" in lower || "people" in lower -> "contacts"
            "play" in lower || "store" in lower || "market" in lower -> "play_store"
            else -> "android"
        }
    }

    private fun guessCategory(pkg: String, label: String): AppCategory {
        val lower = "$pkg $label".lowercase()
        return when {
            "google" in lower || "pixel" in lower -> AppCategory.GOOGLE
            "social" in lower || "facebook" in lower || "whatsapp" in lower || "instagram" in lower || "twitter" in lower || "telegram" in lower || "tiktok" in lower || "reddit" in lower -> AppCategory.SOCIAL
            "game" in lower || "play" in lower || "arcade" in lower || "puzzle" in lower || "subway" in lower || "candy" in lower -> AppCategory.GAMES
            "call" in lower || "phone" in lower || "message" in lower || "mail" in lower || "chat" in lower || "discord" in lower -> AppCategory.COMMUNICATION
            "camera" in lower || "photo" in lower || "music" in lower || "video" in lower || "youtube" in lower || "media" in lower || "spotify" in lower || "netflix" in lower -> AppCategory.MEDIA
            "calc" in lower || "clock" in lower || "file" in lower || "recorder" in lower || "tool" in lower || "weather" in lower -> AppCategory.UTILITIES
            "doc" in lower || "sheet" in lower || "drive" in lower || "note" in lower || "office" in lower || "calendar" in lower || "keep" in lower -> AppCategory.PRODUCTIVITY
            "setting" in lower || "system" in lower || "permission" in lower -> AppCategory.SYSTEM
            else -> AppCategory.ALL
        }
    }

    suspend fun recordAppLaunch(packageName: String) = withContext(Dispatchers.IO) {
        launcherDao.recordAppLaunch(packageName, System.currentTimeMillis())
    }

    suspend fun pinAppToDesktop(app: AppModel, pageIndex: Int, cellX: Int, cellY: Int) = withContext(Dispatchers.IO) {
        val entity = AppItemEntity(
            packageName = app.packageName,
            activityName = app.activityName,
            customLabel = if (app.label != app.defaultLabel) app.label else null,
            defaultLabel = app.defaultLabel,
            pageIndex = pageIndex,
            cellX = cellX,
            cellY = cellY,
            isDock = false,
            folderId = null,
            isHidden = false,
            isPinned = true,
            iconKey = app.systemIconKey
        )
        launcherDao.insertAppItem(entity)
    }

    suspend fun unpinAppFromDesktop(packageName: String) = withContext(Dispatchers.IO) {
        launcherDao.removeAppFromDesktop(packageName)
    }

    suspend fun pinAppToDock(app: AppModel, targetSlot: Int) = withContext(Dispatchers.IO) {
        val boundedSlot = targetSlot.coerceIn(0, 4)
        val currentDock = dockAppsFlow.first()
        val existingAtTarget = currentDock.find { it.cellX == boundedSlot }
        val existingAtOtherSlot = currentDock.find { it.packageName == app.packageName }

        if (existingAtOtherSlot != null && existingAtTarget != null && existingAtOtherSlot.packageName != existingAtTarget.packageName) {
            // Swap slots
            launcherDao.updateDockSlot(existingAtTarget.packageName, existingAtOtherSlot.cellX)
            launcherDao.updateDockSlot(app.packageName, boundedSlot)
            return@withContext
        }

        if (existingAtTarget != null && existingAtTarget.packageName != app.packageName) {
            // Move displaced app to desktop
            launcherDao.unpinFromDock(existingAtTarget.packageName)
        }

        val updated = launcherDao.updateAppToDockSlot(app.packageName, boundedSlot)
        if (updated == 0) {
            launcherDao.insertAppItem(
                AppItemEntity(
                    packageName = app.packageName,
                    activityName = app.activityName,
                    customLabel = if (app.label != app.defaultLabel) app.label else null,
                    defaultLabel = app.defaultLabel,
                    pageIndex = -1,
                    cellX = boundedSlot,
                    cellY = 0,
                    isDock = true,
                    folderId = null,
                    isHidden = false,
                    isPinned = true,
                    iconKey = app.systemIconKey
                )
            )
        }
    }

    suspend fun unpinAppFromDock(packageName: String) = withContext(Dispatchers.IO) {
        launcherDao.unpinFromDock(packageName)
    }

    suspend fun reorderDockSlots(fromSlot: Int, toSlot: Int) = withContext(Dispatchers.IO) {
        if (fromSlot == toSlot) return@withContext
        val currentDock = dockAppsFlow.first()
        val fromApp = currentDock.find { it.cellX == fromSlot } ?: return@withContext
        val toApp = currentDock.find { it.cellX == toSlot }

        if (toApp != null) {
            launcherDao.updateDockSlot(toApp.packageName, fromSlot)
        }
        launcherDao.updateDockSlot(fromApp.packageName, toSlot)
    }

    suspend fun setAppHidden(packageName: String, hidden: Boolean) = withContext(Dispatchers.IO) {
        launcherDao.setAppHidden(packageName, hidden)
    }

    suspend fun renameApp(packageName: String, newLabel: String?) = withContext(Dispatchers.IO) {
        launcherDao.updateAppCustomLabel(packageName, newLabel)
    }

    suspend fun createFolder(title: String, pageIndex: Int, cellX: Int, cellY: Int, appPackageNames: List<String>) = withContext(Dispatchers.IO) {
        val folderEntity = FolderEntity(
            title = title,
            pageIndex = pageIndex,
            cellX = cellX,
            cellY = cellY,
            isDock = false
        )
        val folderId = launcherDao.insertFolder(folderEntity)
        appPackageNames.forEach { pkg ->
            val entity = AppItemEntity(
                packageName = pkg,
                defaultLabel = pkg,
                pageIndex = pageIndex,
                cellX = 0,
                cellY = 0,
                isDock = false,
                folderId = folderId,
                isPinned = true
            )
            launcherDao.insertAppItem(entity)
        }
    }

    suspend fun deleteFolder(folderId: Long) = withContext(Dispatchers.IO) {
        launcherDao.releaseAppsFromFolder(folderId)
        launcherDao.deleteFolderById(folderId)
    }

    suspend fun createDockFolder(slotIndex: Int, app1Package: String, app2Package: String, title: String = "Folder") = withContext(Dispatchers.IO) {
        val boundedSlot = slotIndex.coerceIn(0, 4)
        val folderEntity = FolderEntity(
            title = title,
            pageIndex = -1,
            cellX = boundedSlot,
            cellY = 0,
            isDock = true
        )
        val folderId = launcherDao.insertFolder(folderEntity)
        listOf(app1Package, app2Package).forEach { pkg ->
            launcherDao.setAppFolder(pkg, folderId)
            launcherDao.updateAppToDockSlot(pkg, boundedSlot)
        }
    }

    suspend fun addAppToFolder(folderId: Long, packageName: String) = withContext(Dispatchers.IO) {
        launcherDao.setAppFolder(packageName, folderId)
    }

    suspend fun removeAppFromFolder(packageName: String) = withContext(Dispatchers.IO) {
        launcherDao.setAppFolder(packageName, null)
    }

    suspend fun setDockStyle(style: String) = withContext(Dispatchers.IO) {
        updateSetting("dock_style", style)
    }

    suspend fun setDoubleTapToSleep(enabled: Boolean) = withContext(Dispatchers.IO) {
        updateSetting("double_tap_to_sleep", enabled.toString())
    }

    suspend fun setSwipeDownGesture(action: String) = withContext(Dispatchers.IO) {
        updateSetting("swipe_down_gesture", action)
    }

    suspend fun setSearchEngine(engine: String) = withContext(Dispatchers.IO) {
        updateSetting("search_engine", engine)
    }

    suspend fun autoGroupAppsByCategory() = withContext(Dispatchers.IO) {
        val currentApps = allAppsFlow.first()
        launcherDao.clearFolders()
        launcherDao.clearApps()

        // 1. Re-insert Dock apps
        val dockApps = currentApps.filter { it.isDock }
        dockApps.forEach { dockApp ->
            val entity = AppItemEntity(
                packageName = dockApp.packageName,
                activityName = dockApp.activityName,
                defaultLabel = dockApp.defaultLabel,
                customLabel = if (dockApp.label != dockApp.defaultLabel) dockApp.label else null,
                pageIndex = -1,
                cellX = dockApp.cellX,
                cellY = dockApp.cellY,
                isDock = true,
                folderId = null,
                isHidden = dockApp.isHidden,
                isPinned = true,
                iconKey = dockApp.systemIconKey
            )
            launcherDao.insertAppItem(entity)
        }

        // 2. Group non-dock, non-hidden apps by functional category
        val appsToGroup = currentApps.filterNot { it.isDock || it.isHidden }
        val categorized = appsToGroup.groupBy { it.category }

        var currentX = 0
        var currentY = 1 // Start below At a Glance widget
        var currentPage = 0

        val categoryOrder = listOf(
            AppCategory.GOOGLE,
            AppCategory.COMMUNICATION,
            AppCategory.MEDIA,
            AppCategory.PRODUCTIVITY,
            AppCategory.UTILITIES,
            AppCategory.SYSTEM
        )

        categoryOrder.forEach { category ->
            val groupApps = categorized[category] ?: emptyList()
            if (groupApps.isNotEmpty()) {
                val folderEntity = FolderEntity(
                    title = category.title,
                    pageIndex = currentPage,
                    cellX = currentX,
                    cellY = currentY,
                    isDock = false,
                    colorTag = "primary"
                )
                val folderId = launcherDao.insertFolder(folderEntity)

                groupApps.forEach { app ->
                    val entity = AppItemEntity(
                        packageName = app.packageName,
                        activityName = app.activityName,
                        defaultLabel = app.defaultLabel,
                        customLabel = if (app.label != app.defaultLabel) app.label else null,
                        pageIndex = currentPage,
                        cellX = 0,
                        cellY = 0,
                        isDock = false,
                        folderId = folderId,
                        isHidden = false,
                        isPinned = true,
                        iconKey = app.systemIconKey
                    )
                    launcherDao.insertAppItem(entity)
                }

                currentX++
                if (currentX >= 4) {
                    currentX = 0
                    currentY++
                    if (currentY >= 4) {
                        currentY = 1
                        currentPage++
                    }
                }
            }
        }
    }

    suspend fun resetAppGrouping() = withContext(Dispatchers.IO) {
        launcherDao.clearFolders()
        launcherDao.clearApps()
        initializeDefaultLayoutIfNeeded()
    }

    suspend fun addWidget(type: WidgetType, pageIndex: Int, cellX: Int, cellY: Int) = withContext(Dispatchers.IO) {
        val entity = WidgetEntity(
            widgetType = type.name,
            pageIndex = pageIndex,
            cellX = cellX,
            cellY = cellY,
            spanX = type.defaultSpanX,
            spanY = type.defaultSpanY
        )
        launcherDao.insertWidget(entity)
    }

    suspend fun removeWidget(widgetId: Long) = withContext(Dispatchers.IO) {
        launcherDao.deleteWidgetById(widgetId)
    }

    suspend fun updateSetting(key: String, value: String) = withContext(Dispatchers.IO) {
        launcherDao.setSetting(LauncherSettingEntity(key, value))
    }

    fun getGoogleDiscoverFeed(): List<DiscoverNewsItem> {
        return listOf(
            DiscoverNewsItem(
                id = "disc_1",
                title = "Google reveals Pixel 10 Pro camera breakthrough and Tensor G5 architecture",
                publisher = "9to5Google",
                timeAgo = "2h ago",
                category = "Pixel News",
                summary = "The upcoming Pixel 10 series introduces next-generation on-device AI photography, customized TSMC 3nm silicon, and redesigned Material You widgets.",
                articleUrl = "https://9to5google.com",
                likes = 842,
                drawableRes = R.drawable.pixel_wallpaper_obsidian_1791269886355
            ),
            DiscoverNewsItem(
                id = "disc_2",
                title = "Android 16: Top 10 subtle features transforming how you use your phone",
                publisher = "The Verge",
                timeAgo = "4h ago",
                category = "Android",
                summary = "From predictive back gesture fluidity to enhanced At a Glance live lock-screen activities and dynamic audio spatialization.",
                articleUrl = "https://theverge.com",
                likes = 430,
                drawableRes = R.drawable.pixel_wallpaper_meadow_1791269899199
            ),
            DiscoverNewsItem(
                id = "disc_3",
                title = "Google Gemini Live: Advanced multi-modal assistant arrives on all Pixel devices",
                publisher = "Android Central",
                timeAgo = "6h ago",
                category = "AI & Tech",
                summary = "Real-time natural conversational capabilities, live vision parsing, and seamless workspace integrations.",
                articleUrl = "https://androidcentral.com",
                likes = 1205,
                drawableRes = R.drawable.pixel_wallpaper_sunset_1791269910743
            ),
            DiscoverNewsItem(
                id = "disc_4",
                title = "Premier League: Stunning stoppage-time header seals dramatic London derby victory",
                publisher = "BBC Sport",
                timeAgo = "1h ago",
                category = "Sports",
                summary = "A breath-taking 94th minute strike delivered 3 points in one of the most thrilling matches of the current football season.",
                articleUrl = "https://bbc.com/sport",
                likes = 619
            ),
            DiscoverNewsItem(
                id = "disc_5",
                title = "NASA James Webb telescope captures unprecedented spiral galaxy details",
                publisher = "Astronomy Now",
                timeAgo = "8h ago",
                category = "Science",
                summary = "Infrared instruments reveal newborn star clusters and dust lanes with staggering clarity 25 million light-years away.",
                articleUrl = "https://nasa.gov",
                likes = 952
            )
        )
    }

    fun getWeatherData(): PixelWeatherData {
        return PixelWeatherData()
    }
}
