package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCategory
import com.example.model.AppModel
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.PixelDrawerBackground
import kotlinx.coroutines.launch

/**
 * Classic Pixel App Drawer with Horizontal Paging.
 * Features top search bar, category filters, page-by-page horizontal swiping
 * with page indicators, and standard full-color icon grid.
 */
@Composable
fun VerticalAppDrawer(
    apps: List<AppModel>,
    allApps: List<AppModel>,
    searchQuery: String,
    selectedCategory: AppCategory = AppCategory.ALL,
    useThemedIcons: Boolean = false,
    iconShape: String = "SQUIRCLE",
    gridColumns: Int = 4,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (AppCategory) -> Unit = {},
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit = {},
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val appsPerPage = gridColumns * 5 // 20 apps per page (4x5 grid)

    val topSuggestedApps = remember(allApps) {
        allApps.sortedByDescending { it.launchCount }.take(gridColumns)
    }

    val showSuggested = searchQuery.isBlank() && selectedCategory == AppCategory.ALL && topSuggestedApps.isNotEmpty()

    // Divide apps into horizontal pages
    val pages = remember(apps, appsPerPage) {
        if (apps.isEmpty()) listOf(emptyList())
        else apps.chunked(appsPerPage)
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PixelDrawerBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("vertical_app_drawer")
            .draggable(
                state = rememberDraggableState { delta ->
                    if (delta > 35) {
                        onCloseDrawer()
                    }
                },
                orientation = Orientation.Vertical
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Drag handle pill at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 5.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f))
                        .clickable(onClick = onCloseDrawer)
                        .testTag("app_drawer_drag_handle")
                )
            }

            // Top Search Bar (Classic Pixel Input)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp)
                    .testTag("app_drawer_search_bar"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("app_drawer_search_input"),
                    placeholder = {
                        Text(
                            "Search apps...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = GoogleBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AppCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(category) },
                        label = {
                            Text(
                                text = category.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Suggested Apps Bar (Shown above pages when search is blank)
            if (showSuggested) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Suggested",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        topSuggestedApps.forEach { app ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                PixelAppIcon(
                                    app = app,
                                    useThemedIcons = useThemedIcons,
                                    iconShape = iconShape,
                                    showLabel = true,
                                    iconSize = 50.dp,
                                    onClick = { onAppClick(app) },
                                    onLongClick = { onAppLongClick(app) }
                                )
                            }
                        }
                        repeat(gridColumns - topSuggestedApps.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // Horizontal Pager for Page-by-Page Swiping
            if (apps.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("app_drawer_pager")
                ) { pageIndex ->
                    val pageApps = pages.getOrElse(pageIndex) { emptyList() }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridColumns),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = pageApps,
                            key = { it.packageName },
                            contentType = { "app" }
                        ) { app ->
                            PixelAppIcon(
                                app = app,
                                useThemedIcons = useThemedIcons,
                                iconShape = iconShape,
                                showLabel = true,
                                iconSize = 54.dp,
                                onClick = { onAppClick(app) },
                                onLongClick = { onAppLongClick(app) }
                            )
                        }
                    }
                }
            } else {
                // Empty state if search or category has no items
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No apps found",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Page Indicator Dots
            if (pages.size > 1 && searchQuery.isBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("app_drawer_page_indicators"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val dotWidth = androidx.compose.animation.core.animateDpAsState(
                            targetValue = if (isSelected) 22.dp else 8.dp,
                            label = "dot_width"
                        ).value
                        val dotColor = androidx.compose.animation.animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.35f),
                            label = "dot_color"
                        ).value
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = dotWidth, height = 8.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
