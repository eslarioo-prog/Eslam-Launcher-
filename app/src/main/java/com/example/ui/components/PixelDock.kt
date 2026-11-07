package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppModel
import com.example.model.FolderModel
import com.example.ui.theme.GoogleBlue
import com.example.ui.theme.GoogleGreen
import com.example.ui.theme.GoogleRed
import com.example.ui.theme.GoogleYellow

@Composable
fun GoogleGLogo(modifier: Modifier = Modifier) {
    // Pixel signature 4-color G logo badge
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(
                Brush.sweepGradient(
                    listOf(
                        GoogleRed,
                        GoogleYellow,
                        GoogleGreen,
                        GoogleBlue,
                        GoogleRed
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Text(
            text = "G",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun PixelSearchBar(
    searchEngine: String = "GOOGLE",
    onClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onLensClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("pixel_search_bar")
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(containerColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (searchEngine == "GEMINI_LIVE") {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else {
                GoogleGLogo()
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (searchEngine == "GEMINI_LIVE") "Ask Gemini Live or search..." else "Search phone and web...",
                color = contentColor.copy(alpha = 0.75f),
                fontSize = 14.sp,
                maxLines = 1
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Google Lens
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onLensClick)
                    .testTag("search_lens_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = "Google Lens",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Google Voice Mic
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onVoiceClick)
                    .testTag("search_voice_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice search",
                    tint = GoogleBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Persistent Pixel Dock supporting up to 5 frequently used applications
 * with interactive drag-and-drop, slot highlighting, and empty slot pinning.
 */
@Composable
fun PixelDock(
    dockApps: List<AppModel>,
    dockFolders: List<FolderModel> = emptyList(),
    useThemedIcons: Boolean,
    iconShape: String = "SQUIRCLE",
    dockStyle: String = "PILL_BLUR",
    searchEngine: String = "GOOGLE",
    showSearchBar: Boolean,
    hoveredSlotIndex: Int? = null,
    draggedApp: AppModel? = null,
    onAppClick: (AppModel) -> Unit,
    onAppLongClick: (AppModel) -> Unit,
    onFolderClick: (FolderModel) -> Unit = {},
    onAppDragStart: (AppModel, Int, Offset) -> Unit = { _, _, _ -> },
    onAppDrag: (Offset) -> Unit = {},
    onAppDragEnd: () -> Unit = {},
    onAppDragCancel: () -> Unit = {},
    onEmptySlotClick: (Int) -> Unit = {},
    onSlotPositioned: (Int, Rect) -> Unit = { _, _ -> },
    onSearchClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onLensClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val maskShape = remember(iconShape) { getPixelMaskShape(iconShape) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("pixel_dock"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val dockContainerModifier = when (dockStyle) {
            "PILL_BLUR" -> Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(36.dp), clip = false)
                .clip(RoundedCornerShape(36.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(36.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
            "SOLID_CARD" -> Modifier
                .fillMaxWidth()
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
            else -> Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        }

        // Dock 5 Favorite Apps Row
        Row(
            modifier = dockContainerModifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (slotIndex in 0 until 5) {
                // Find folder or app occupying this exact slot index (cellX)
                val dockFolder = dockFolders.find { it.isDock && it.cellX == slotIndex }
                val appInSlot = if (dockFolder == null) {
                    dockApps.find { it.cellX == slotIndex }
                        ?: if (dockApps.none { it.cellX == slotIndex }) dockApps.getOrNull(slotIndex) else null
                } else null

                val isHovered = hoveredSlotIndex == slotIndex
                val isBeingDragged = draggedApp != null && appInSlot != null && draggedApp.packageName == appInSlot.packageName

                val slotScale by animateFloatAsState(
                    targetValue = if (isHovered) 1.12f else 1.0f,
                    animationSpec = spring(),
                    label = "slotScale"
                )

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .scale(slotScale)
                        .onGloballyPositioned { coordinates ->
                            onSlotPositioned(slotIndex, coordinates.boundsInWindow())
                        }
                        .testTag("dock_slot_$slotIndex"),
                    contentAlignment = Alignment.Center
                ) {
                    if (dockFolder != null) {
                        // Slot contains a Folder
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .shadow(elevation = if (isHovered) 6.dp else 2.dp, shape = maskShape, clip = false)
                                .clip(maskShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f))
                                .border(
                                    width = if (isHovered) 2.dp else 1.dp,
                                    color = if (isHovered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                    shape = maskShape
                                )
                                .clickable { onFolderClick(dockFolder) }
                                .testTag("dock_folder_$slotIndex"),
                            contentAlignment = Alignment.Center
                        ) {
                            val previewApps = dockFolder.apps.take(4)
                            Column(
                                modifier = Modifier.padding(5.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    previewApps.getOrNull(0)?.let { a ->
                                        PixelAppIcon(app = a, useThemedIcons = useThemedIcons, iconShape = iconShape, showLabel = false, iconSize = 18.dp, onClick = { onFolderClick(dockFolder) }, onLongClick = {})
                                    }
                                    previewApps.getOrNull(1)?.let { a ->
                                        PixelAppIcon(app = a, useThemedIcons = useThemedIcons, iconShape = iconShape, showLabel = false, iconSize = 18.dp, onClick = { onFolderClick(dockFolder) }, onLongClick = {})
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    previewApps.getOrNull(2)?.let { a ->
                                        PixelAppIcon(app = a, useThemedIcons = useThemedIcons, iconShape = iconShape, showLabel = false, iconSize = 18.dp, onClick = { onFolderClick(dockFolder) }, onLongClick = {})
                                    }
                                    previewApps.getOrNull(3)?.let { a ->
                                        PixelAppIcon(app = a, useThemedIcons = useThemedIcons, iconShape = iconShape, showLabel = false, iconSize = 18.dp, onClick = { onFolderClick(dockFolder) }, onLongClick = {})
                                    }
                                }
                            }

                            if (isHovered) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(maskShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                                )
                            }
                        }
                    } else if (appInSlot != null && !isBeingDragged) {
                        // Slot contains an app
                        var iconPos by remember { androidx.compose.runtime.mutableStateOf(Offset.Zero) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coords ->
                                    iconPos = coords.boundsInWindow().topLeft
                                }
                                .pointerInput(appInSlot.packageName) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { offset ->
                                            onAppDragStart(appInSlot, slotIndex, iconPos + offset)
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            onAppDrag(dragAmount)
                                        },
                                        onDragEnd = { onAppDragEnd() },
                                        onDragCancel = { onAppDragCancel() }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            PixelAppIcon(
                                app = appInSlot,
                                useThemedIcons = useThemedIcons,
                                iconShape = iconShape,
                                showLabel = false,
                                iconSize = 54.dp,
                                onClick = { onAppClick(appInSlot) },
                                onLongClick = { onAppLongClick(appInSlot) }
                            )

                            // Hover halo when another app is dragged over this slot
                            if (isHovered) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(maskShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = maskShape
                                        )
                                )
                            }
                        }
                    } else {
                        // Empty slot placeholder or currently dragged app source placeholder
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .shadow(elevation = if (isHovered) 4.dp else 1.dp, shape = maskShape, clip = false)
                                .clip(maskShape)
                                .background(
                                    if (isHovered) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    width = if (isHovered) 2.dp else 1.2.dp,
                                    color = if (isHovered) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    shape = maskShape
                                )
                                .clickable { onEmptySlotClick(slotIndex) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Pin application to slot ${slotIndex + 1}",
                                tint = if (isHovered) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Universal Search Bar
        if (showSearchBar) {
            PixelSearchBar(
                searchEngine = searchEngine,
                onClick = onSearchClick,
                onVoiceClick = onVoiceClick,
                onLensClick = onLensClick
            )
        }
    }
}
