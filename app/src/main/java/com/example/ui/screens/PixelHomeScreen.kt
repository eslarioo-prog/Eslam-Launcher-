package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import com.example.ui.components.getPixelMaskShape
import com.example.ui.components.getStockAppColor
import com.example.ui.components.getSystemIconForApp
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.AppModel
import com.example.model.FolderModel
import com.example.model.WidgetModel
import com.example.ui.components.AtAGlanceWidget
import com.example.ui.components.PixelAppIcon
import com.example.ui.components.PixelContextMenuDialog
import com.example.ui.components.PixelDock
import com.example.ui.components.PixelFolderDialog
import com.example.ui.components.PixelWidgetContainer
import com.example.ui.components.QuickSettingsShade
import com.example.ui.components.UniversalSearchOverlay
import com.example.viewmodel.LauncherSheet
import com.example.viewmodel.LauncherViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PixelHomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val config by viewModel.launcherConfig.collectAsStateWithLifecycle()
    val desktopApps by viewModel.desktopApps.collectAsStateWithLifecycle()
    val dockApps by viewModel.dockApps.collectAsStateWithLifecycle()
    val allApps by viewModel.allApps.collectAsStateWithLifecycle()
    val filteredDrawerApps by viewModel.filteredDrawerApps.collectAsStateWithLifecycle()
    val hiddenApps by viewModel.hiddenApps.collectAsStateWithLifecycle()
    val folders by viewModel.folders.collectAsStateWithLifecycle()
    val widgets by viewModel.widgets.collectAsStateWithLifecycle()
    val weatherData by viewModel.weatherData.collectAsStateWithLifecycle()
    val discoverNews by viewModel.discoverNews.collectAsStateWithLifecycle()

    val isDrawerOpen by viewModel.isDrawerOpen.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val activeFolder by viewModel.activeFolder.collectAsStateWithLifecycle()
    val selectedContextApp by viewModel.selectedAppForContext.collectAsStateWithLifecycle()
    val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()

    var showCustomizationMenu by remember { mutableStateOf(false) }
    var renameDialogApp by remember { mutableStateOf<AppModel?>(null) }
    var renameInputText by remember { mutableStateOf("") }

    // Persistent Dock Drag & Drop and Slot Management State
    var draggedApp by remember { mutableStateOf<AppModel?>(null) }
    var sourceDockSlot by remember { mutableStateOf<Int?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var hoveredDockSlot by remember { mutableStateOf<Int?>(null) }
    val dockSlotBounds = remember { mutableStateMapOf<Int, Rect>() }
    var pickAppForSlotIndex by remember { mutableStateOf<Int?>(null) }

    var isSleeping by remember { mutableStateOf(false) }

    // 3 Pages: 0 = Discover Feed, 1 = Main Desktop, 2 = Extended Widgets
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })

    // Wallpaper Resource
    val wallpaperRes = remember(config.wallpaperId) {
        when (config.wallpaperId) {
            "meadow" -> R.drawable.pixel_wallpaper_meadow_1791269899199
            "sunset" -> R.drawable.pixel_wallpaper_sunset_1791269910743
            else -> R.drawable.pixel_wallpaper_obsidian_1791269886355
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("pixel_home_screen")
            .then(
                if (!isDrawerOpen) {
                    Modifier.pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            if (dragAmount < -36) {
                                viewModel.openDrawer()
                            } else if (dragAmount > 40 && pagerState.currentPage != 0) {
                                if (config.swipeDownGesture == "NOTIFICATIONS") {
                                    viewModel.openSheet(LauncherSheet.QuickSettingsShade)
                                } else if (config.swipeDownGesture == "APP_DRAWER") {
                                    viewModel.openDrawer()
                                }
                            }
                        }
                    }
                } else Modifier
            )
    ) {
        // Original Pixel Live Wallpaper Layer
        com.example.ui.components.PixelLiveWallpaperCanvas(
            modifier = Modifier.fillMaxSize()
        )

        // Smooth scrim when App Drawer is opened
        if (isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
            )
        }

        // Subtle gradient overlay for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.45f)
                        )
                    )
                )
        )

        // Main Pager Screens
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> {
                        // Google Discover Feed (Page -1)
                        GoogleDiscoverScreen(
                            newsList = discoverNews,
                            onOpenSearch = { viewModel.openSheet(LauncherSheet.UniversalSearch) }
                        )
                    }
                    1 -> {
                        // Main Home Desktop (Page 0)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .combinedClickable(
                                    onClick = {},
                                    onDoubleClick = {
                                        if (config.doubleTapToSleep) {
                                            isSleeping = true
                                        }
                                    },
                                    onLongClick = { showCustomizationMenu = true }
                                )
                                .padding(horizontal = 12.dp)
                        ) {
                            // Top At a Glance
                            AtAGlanceWidget(
                                weatherData = weatherData,
                                showWeather = config.showAtAGlanceWeather,
                                showCalendar = config.showAtAGlanceCalendar,
                                showBattery = config.showAtAGlanceBattery,
                                onDateClick = {
                                    val calendarIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                                        addCategory(android.content.Intent.CATEGORY_APP_CALENDAR)
                                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    try {
                                        context.startActivity(calendarIntent)
                                    } catch (e: Exception) {
                                    }
                                },
                                onWeatherClick = { viewModel.openSheet(LauncherSheet.WeatherForecast) }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Main Desktop Grid (4x5)
                            val page0Apps = remember(desktopApps) { desktopApps.filter { it.pageIndex == 0 } }
                            val page0Folders = remember(folders) { folders.filter { it.pageIndex == 0 } }

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(config.gridCols),
                                userScrollEnabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Folders
                                items(
                                    items = page0Folders,
                                    key = { it.id },
                                    contentType = { "folder" }
                                ) { folder ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable { viewModel.openFolder(folder) }
                                            .padding(4.dp)
                                    ) {
                                        val folderShape = com.example.ui.components.getPixelMaskShape(config.iconShape)
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .shadow(3.dp, folderShape, clip = false)
                                                .clip(folderShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f))
                                                .border(
                                                    width = 0.5.dp,
                                                    color = if (config.useThemedIcons) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                                    else Color.White.copy(alpha = 0.15f),
                                                    shape = folderShape
                                                )
                                                .padding(6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            LazyVerticalGrid(
                                                columns = GridCells.Fixed(2),
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                verticalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                items(folder.apps.take(4)) { app ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(18.dp)
                                                            .clip(folderShape)
                                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = com.example.ui.components.getSystemIconForApp(app.systemIconKey),
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(10.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = folder.title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )
                                    }
                                }

                                // Desktop Apps
                                items(
                                    items = page0Apps,
                                    key = { it.packageName },
                                    contentType = { "desktop_app" }
                                ) { app ->
                                    var itemPos by remember { mutableStateOf(Offset.Zero) }
                                    Box(
                                        modifier = Modifier
                                            .onGloballyPositioned { coords -> itemPos = coords.boundsInWindow().topLeft }
                                            .pointerInput(app.packageName) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = { offset ->
                                                        draggedApp = app
                                                        sourceDockSlot = null
                                                        dragPosition = itemPos + offset
                                                    },
                                                    onDrag = { change, dragAmount ->
                                                        change.consume()
                                                        dragPosition += dragAmount
                                                        hoveredDockSlot = dockSlotBounds.entries.firstOrNull { (_, rect) ->
                                                            rect.contains(dragPosition)
                                                        }?.key
                                                    },
                                                    onDragEnd = {
                                                        val target = hoveredDockSlot
                                                        if (target != null && draggedApp != null) {
                                                            viewModel.pinAppToDock(draggedApp!!, target)
                                                            Toast.makeText(context, "Pinned ${draggedApp?.label} to Dock slot ${target + 1}", Toast.LENGTH_SHORT).show()
                                                        }
                                                        draggedApp = null
                                                        sourceDockSlot = null
                                                        hoveredDockSlot = null
                                                    },
                                                    onDragCancel = {
                                                        draggedApp = null
                                                        sourceDockSlot = null
                                                        hoveredDockSlot = null
                                                    }
                                                )
                                            }
                                    ) {
                                        PixelAppIcon(
                                            app = app,
                                            useThemedIcons = config.useThemedIcons,
                                            iconShape = config.iconShape,
                                            showLabel = true,
                                            iconSize = 54.dp,
                                            onClick = { viewModel.launchApp(context, app) },
                                            onLongClick = { viewModel.openContextMenu(app) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // Extended Widgets Page (Page 1)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .combinedClickable(
                                    onClick = {},
                                    onDoubleClick = {
                                        if (config.doubleTapToSleep) {
                                            isSleeping = true
                                        }
                                    },
                                    onLongClick = { showCustomizationMenu = true }
                                )
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Pixel Dynamic Widgets",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(start = 6.dp, top = 8.dp, bottom = 12.dp)
                            )

                            val page1Widgets = remember(widgets) { widgets.filter { it.pageIndex == 1 } }
                            val page1Apps = remember(desktopApps) { desktopApps.filter { it.pageIndex == 1 } }

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(1),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(
                                    items = page1Widgets,
                                    key = { it.id },
                                    contentType = { "widget_${it.type.name}" }
                                ) { widget ->
                                    PixelWidgetContainer(
                                        widget = widget,
                                        weatherData = weatherData,
                                        onRemove = { viewModel.removeWidget(widget.id) },
                                        onOpenWeather = { viewModel.openSheet(LauncherSheet.WeatherForecast) }
                                    )
                                }

                                items(
                                    items = page1Apps,
                                    key = { it.packageName },
                                    contentType = { "desktop_app" }
                                ) { app ->
                                    var itemPos by remember { mutableStateOf(Offset.Zero) }
                                    Box(
                                        modifier = Modifier
                                            .onGloballyPositioned { coords -> itemPos = coords.boundsInWindow().topLeft }
                                            .pointerInput(app.packageName) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = { offset ->
                                                        draggedApp = app
                                                        sourceDockSlot = null
                                                        dragPosition = itemPos + offset
                                                    },
                                                    onDrag = { change, dragAmount ->
                                                        change.consume()
                                                        dragPosition += dragAmount
                                                        hoveredDockSlot = dockSlotBounds.entries.firstOrNull { (_, rect) ->
                                                            rect.contains(dragPosition)
                                                        }?.key
                                                    },
                                                    onDragEnd = {
                                                        val target = hoveredDockSlot
                                                        if (target != null && draggedApp != null) {
                                                            viewModel.pinAppToDock(draggedApp!!, target)
                                                            Toast.makeText(context, "Pinned ${draggedApp?.label} to Dock slot ${target + 1}", Toast.LENGTH_SHORT).show()
                                                        }
                                                        draggedApp = null
                                                        sourceDockSlot = null
                                                        hoveredDockSlot = null
                                                    },
                                                    onDragCancel = {
                                                        draggedApp = null
                                                        sourceDockSlot = null
                                                        hoveredDockSlot = null
                                                    }
                                                )
                                            }
                                    ) {
                                        PixelAppIcon(
                                            app = app,
                                            useThemedIcons = config.useThemedIcons,
                                            iconShape = config.iconShape,
                                            showLabel = true,
                                            iconSize = 54.dp,
                                            onClick = { viewModel.launchApp(context, app) },
                                            onLongClick = { viewModel.openContextMenu(app) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Pager Indicator Pill Dots (Page 0, 1, 2)
            if (pagerState.currentPage != 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(width = if (isSelected) 18.dp else 6.dp, height = 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.4f))
                        )
                    }
                }
            }

            // Bottom Pixel Dock (5 Favorite Apps + Universal Search Bar)
            if (pagerState.currentPage != 0) {
                // Swipe up indicator handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openDrawer() }
                        .padding(top = 4.dp, bottom = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 36.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.5f))
                    )
                }

                PixelDock(
                    dockApps = dockApps,
                    dockFolders = folders.filter { it.isDock },
                    useThemedIcons = config.useThemedIcons,
                    iconShape = config.iconShape,
                    dockStyle = config.dockStyle,
                    searchEngine = config.searchEngine,
                    showSearchBar = config.showDockSearch,
                    hoveredSlotIndex = hoveredDockSlot,
                    draggedApp = draggedApp,
                    onAppClick = { viewModel.launchApp(context, it) },
                    onAppLongClick = { viewModel.openContextMenu(it) },
                    onFolderClick = { viewModel.openFolder(it) },
                    onAppDragStart = { app, slotIndex, initialPos ->
                        draggedApp = app
                        sourceDockSlot = slotIndex
                        dragPosition = initialPos
                    },
                    onAppDrag = { dragAmount ->
                        dragPosition += dragAmount
                        hoveredDockSlot = dockSlotBounds.entries.firstOrNull { (_, rect) ->
                            rect.contains(dragPosition)
                        }?.key
                    },
                    onAppDragEnd = {
                        val target = hoveredDockSlot
                        val source = sourceDockSlot
                        if (target != null && draggedApp != null) {
                            val targetFolder = folders.find { it.isDock && it.cellX == target }
                            val existingAppInSlot = dockApps.find { it.cellX == target }

                            if (targetFolder != null) {
                                viewModel.addAppToFolder(targetFolder.id, draggedApp!!.packageName)
                                Toast.makeText(context, "Added ${draggedApp?.label} to ${targetFolder.title}", Toast.LENGTH_SHORT).show()
                            } else if (existingAppInSlot != null && existingAppInSlot.packageName != draggedApp!!.packageName && source == null) {
                                val smartTitle = viewModel.suggestSmartFolderName(existingAppInSlot, draggedApp!!)
                                viewModel.createDockFolder(target, existingAppInSlot.packageName, draggedApp!!.packageName, smartTitle)
                                Toast.makeText(context, "Created Smart Folder '$smartTitle'", Toast.LENGTH_SHORT).show()
                            } else if (source != null) {
                                viewModel.reorderDockSlots(source, target)
                                Toast.makeText(context, "Moved ${draggedApp?.label} to Dock slot ${target + 1}", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.pinAppToDock(draggedApp!!, target)
                                Toast.makeText(context, "Pinned ${draggedApp?.label} to Dock slot ${target + 1}", Toast.LENGTH_SHORT).show()
                            }
                        } else if (source != null && draggedApp != null) {
                            viewModel.unpinAppFromDock(draggedApp!!)
                            Toast.makeText(context, "Removed ${draggedApp?.label} from Dock", Toast.LENGTH_SHORT).show()
                        }
                        draggedApp = null
                        sourceDockSlot = null
                        hoveredDockSlot = null
                    },
                    onAppDragCancel = {
                        draggedApp = null
                        sourceDockSlot = null
                        hoveredDockSlot = null
                    },
                    onEmptySlotClick = { slotIndex ->
                        pickAppForSlotIndex = slotIndex
                    },
                    onSlotPositioned = { slotIndex, rect ->
                        dockSlotBounds[slotIndex] = rect
                    },
                    onSearchClick = { viewModel.openSheet(LauncherSheet.UniversalSearch) },
                    onVoiceClick = {
                        Toast.makeText(context, "Listening for Google Voice Search...", Toast.LENGTH_SHORT).show()
                        viewModel.openSheet(LauncherSheet.UniversalSearch)
                    },
                    onLensClick = {
                        Toast.makeText(context, "Opening Google Lens Visual Search...", Toast.LENGTH_SHORT).show()
                        viewModel.openSheet(LauncherSheet.UniversalSearch)
                    }
                )
            }
        }

        // App Drawer Slide-Up Layer
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            PixelAppDrawerScreen(
                apps = filteredDrawerApps,
                allApps = allApps,
                searchQuery = searchQuery,
                selectedCategory = selectedCategory,
                useThemedIcons = config.useThemedIcons,
                iconShape = config.iconShape,
                onSearchChange = { viewModel.setSearchQuery(it) },
                onCategorySelect = { viewModel.setSelectedCategory(it) },
                onAppClick = { app ->
                    viewModel.closeDrawer()
                    viewModel.launchApp(context, app)
                },
                onAppLongClick = { app ->
                    viewModel.openContextMenu(app)
                },
                onCloseDrawer = { viewModel.closeDrawer() }
            )
        }

        // Desktop Customization Menu (Long Press on Empty Screen Space)
        if (showCustomizationMenu) {
            Dialog(onDismissRequest = { showCustomizationMenu = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .shadow(16.dp, RoundedCornerShape(26.dp))
                        .testTag("customization_menu_dialog"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Pixel Home Screen",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    showCustomizationMenu = false
                                    viewModel.openSheet(LauncherSheet.WallpaperAndStyle)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Text("Wallpaper & style", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    showCustomizationMenu = false
                                    viewModel.openSheet(LauncherSheet.WidgetPicker)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Widgets, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Text("Widgets", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    showCustomizationMenu = false
                                    viewModel.autoGroupAppsByCategory()
                                    Toast.makeText(context, "Apps grouped by function into folders", Toast.LENGTH_SHORT).show()
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Auto-group apps by function", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("Group apps into smart category folders", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    showCustomizationMenu = false
                                    viewModel.openSheet(LauncherSheet.Settings)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(14.dp))
                            Text("Home settings", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // App Long Press Context Menu
        selectedContextApp?.let { app ->
            PixelContextMenuDialog(
                app = app,
                onDismiss = { viewModel.closeContextMenu() },
                onPinToHome = { viewModel.pinAppToHome(app) },
                onUnpinFromHome = { viewModel.unpinAppFromHome(app) },
                onPinToDock = {
                    viewModel.pinAppToDock(app)
                    Toast.makeText(context, "Pinned ${app.label} to Dock", Toast.LENGTH_SHORT).show()
                },
                onUnpinFromDock = {
                    viewModel.unpinAppFromDock(app)
                    Toast.makeText(context, "Removed ${app.label} from Dock", Toast.LENGTH_SHORT).show()
                },
                onAppInfo = { viewModel.openAppInfo(context, app) },
                onHideApp = { viewModel.setAppHidden(app, true) },
                onRenameApp = {
                    renameDialogApp = app
                    renameInputText = app.label
                    viewModel.closeContextMenu()
                },
                onLaunchShortcut = { shortcut ->
                    viewModel.launchShortcut(context, app, shortcut)
                }
            )
        }

        // Folder Dialog
        activeFolder?.let { folder ->
            PixelFolderDialog(
                folder = folder,
                useThemedIcons = config.useThemedIcons,
                iconShape = config.iconShape,
                onDismiss = { viewModel.closeFolder() },
                onAppClick = { viewModel.launchApp(context, it) },
                onAppLongClick = { viewModel.openContextMenu(it) },
                onDeleteFolder = { viewModel.deleteFolder(folder.id) }
            )
        }

        // Rename App Dialog
        renameDialogApp?.let { app ->
            Dialog(onDismissRequest = { renameDialogApp = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("rename_app_dialog"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Rename \"${app.defaultLabel}\"",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        OutlinedTextField(
                            value = renameInputText,
                            onValueChange = { renameInputText = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { renameDialogApp = null }) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    viewModel.renameApp(app, renameInputText)
                                    renameDialogApp = null
                                }
                            ) {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }

        // Sheets & Overlays
        when (val sheet = activeSheet) {
            is LauncherSheet.WallpaperAndStyle -> {
                WallpaperStyleScreen(
                    config = config,
                    onWallpaperSelected = { viewModel.updateWallpaper(it) },
                    onPaletteSelected = { viewModel.updatePalette(it) },
                    onThemedIconsToggle = { viewModel.toggleThemedIcons(it) },
                    onIconShapeSelected = { viewModel.setIconShape(it) },
                    onDockStyleSelected = { viewModel.setDockStyle(it) },
                    onGridSizeSelected = { rows, cols -> viewModel.setGridSize(rows, cols) },
                    onDarkModeSelected = { viewModel.setDarkMode(it) },
                    onClose = { viewModel.closeSheet() }
                )
            }
            is LauncherSheet.Settings -> {
                LauncherSettingsScreen(
                    config = config,
                    hiddenApps = hiddenApps,
                    onToggleAtAGlanceWeather = { viewModel.toggleAtAGlanceOption("aag_weather", it) },
                    onToggleAtAGlanceCalendar = { viewModel.toggleAtAGlanceOption("aag_calendar", it) },
                    onToggleAtAGlanceBattery = { viewModel.toggleAtAGlanceOption("aag_battery", it) },
                    onToggleDoubleTapToSleep = { viewModel.setDoubleTapToSleep(it) },
                    onSwipeDownGestureSelected = { viewModel.setSwipeDownGesture(it) },
                    onSearchEngineSelected = { viewModel.setSearchEngine(it) },
                    onUnhideApp = { viewModel.setAppHidden(it, false) },
                    onAutoGroupApps = {
                        viewModel.autoGroupAppsByCategory()
                        Toast.makeText(context, "Apps grouped by function into folders", Toast.LENGTH_SHORT).show()
                    },
                    onResetAppGrouping = {
                        viewModel.resetAppGrouping()
                        Toast.makeText(context, "Layout reset to default", Toast.LENGTH_SHORT).show()
                    },
                    onOpenWallpaperStyle = { viewModel.openSheet(LauncherSheet.WallpaperAndStyle) },
                    onClose = { viewModel.closeSheet() }
                )
            }
            is LauncherSheet.WidgetPicker -> {
                WidgetPickerSheet(
                    onSelectWidget = { viewModel.addWidget(it) },
                    onClose = { viewModel.closeSheet() }
                )
            }
            is LauncherSheet.WeatherForecast -> {
                WeatherForecastSheet(
                    weatherData = weatherData,
                    onClose = { viewModel.closeSheet() }
                )
            }
            is LauncherSheet.QuickSettingsShade -> {
                QuickSettingsShade(
                    onDismiss = { viewModel.closeSheet() },
                    onOpenSettings = { viewModel.openSheet(LauncherSheet.Settings) }
                )
            }
            is LauncherSheet.UniversalSearch -> {
                UniversalSearchOverlay(
                    allApps = allApps,
                    useThemedIcons = config.useThemedIcons,
                    iconShape = config.iconShape,
                    onDismiss = { viewModel.closeSheet() },
                    onAppClick = { viewModel.launchApp(context, it) },
                    onAppLongClick = { viewModel.openContextMenu(it) }
                )
            }
            is LauncherSheet.AppInfoDialog -> {
                Dialog(onDismissRequest = { viewModel.closeSheet() }) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = sheet.app.label, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Package: ${sheet.app.packageName}", fontSize = 12.sp)
                            Text(text = "Category: ${sheet.app.category.title}", fontSize = 12.sp)
                            Text(text = "Launch Count: ${sheet.app.launchCount}", fontSize = 12.sp)
                            TextButton(
                                onClick = { viewModel.closeSheet() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
            is LauncherSheet.RenameAppDialog -> {
                // Handled inline via dialog
            }
            null -> {}
        }

        // Pick App to Pin to Empty Dock Slot Dialog
        pickAppForSlotIndex?.let { slotIndex ->
            val pinnedPackages = dockApps.map { it.packageName }.toSet()
            val availableApps = allApps.filterNot { pinnedPackages.contains(it.packageName) || it.isHidden }

            Dialog(onDismissRequest = { pickAppForSlotIndex = null }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .shadow(16.dp, RoundedCornerShape(28.dp))
                        .testTag("dock_app_picker_dialog"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Pin to Dock Slot ${slotIndex + 1}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Select an app to pin to your favorite dock",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            IconButton(onClick = { pickAppForSlotIndex = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableApps) { app ->
                                PixelAppIcon(
                                    app = app,
                                    useThemedIcons = config.useThemedIcons,
                                    iconShape = config.iconShape,
                                    showLabel = true,
                                    iconSize = 48.dp,
                                    onClick = {
                                        viewModel.pinAppToDock(app, slotIndex)
                                        Toast.makeText(context, "Pinned ${app.label} to Dock slot ${slotIndex + 1}", Toast.LENGTH_SHORT).show()
                                        pickAppForSlotIndex = null
                                    },
                                    onLongClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Dragged App Icon Overlay
        draggedApp?.let { app ->
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (dragPosition.x - 30.dp.roundToPx()).roundToInt(),
                            (dragPosition.y - 30.dp.roundToPx()).roundToInt()
                        )
                    }
                    .size(60.dp)
                    .shadow(16.dp, getPixelMaskShape(config.iconShape), clip = false)
                    .clip(getPixelMaskShape(config.iconShape))
                    .background(
                        if (config.useThemedIcons) MaterialTheme.colorScheme.surfaceVariant
                        else getStockAppColor(app.systemIconKey)
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = getPixelMaskShape(config.iconShape)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getSystemIconForApp(app.systemIconKey),
                    contentDescription = app.label,
                    tint = if (config.useThemedIcons) MaterialTheme.colorScheme.primary else Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Pixel Always-On Display (AOD) Sleep Overlay (Triggered by Double-Tap)
        AnimatedVisibility(
            visible = isSleeping,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            BackHandler { isSleeping = false }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { isSleeping = false }
                    .padding(32.dp)
                    .testTag("pixel_aod_sleep_overlay"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val sdfTime = remember { java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()) }
                    val sdfDate = remember { java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.getDefault()) }
                    val currentTime = remember(isSleeping) { java.util.Date() }

                    Text(
                        text = sdfTime.format(currentTime),
                        color = Color.White,
                        fontSize = 80.sp,
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = (-2).sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${sdfDate.format(currentTime)}  •  ${weatherData.temperature}°C",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Tap anywhere to wake Pixel",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
