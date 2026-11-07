package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.model.AppCategory
import com.example.model.AppModel
import com.example.ui.components.VerticalAppDrawer

@Composable
fun PixelAppDrawerScreen(
    apps: List<AppModel>,
    allApps: List<AppModel>,
    searchQuery: String,
    selectedCategory: AppCategory,
    useThemedIcons: Boolean,
    iconShape: String = "SQUIRCLE",
    onSearchChange: (String) -> Unit,
    onCategorySelect: (AppCategory) -> Unit,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    VerticalAppDrawer(
        apps = apps,
        allApps = allApps,
        searchQuery = searchQuery,
        selectedCategory = selectedCategory,
        useThemedIcons = useThemedIcons,
        iconShape = iconShape,
        onSearchChange = onSearchChange,
        onCategorySelect = onCategorySelect,
        onAppClick = onAppClick,
        onAppLongClick = onAppLongClick,
        onCloseDrawer = onCloseDrawer,
        modifier = modifier
    )
}
