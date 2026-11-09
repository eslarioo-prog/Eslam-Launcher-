package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun QuickSettingsShade(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("h:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }
    val now = Date()
    val timeStr = remember { timeFormat.format(now) }
    val dateStr = remember { dateFormat.format(now) }

    val toggleStates = remember {
        mutableStateMapOf(
            "Internet" to true,
            "Bluetooth" to false,
            "Flashlight" to false,
            "Do Not Disturb" to false
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0C0E10))
                .statusBarsPadding()
                .navigationBarsPadding()
                .clickable(onClick = onDismiss)
                .testTag("quick_settings_shade")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false) {}
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Status Bar: Clock on Left, Indicators on Right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$timeStr  $dateStr",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SignalCellular4Bar,
                            contentDescription = "Signal",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Wi-Fi",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.BatteryFull,
                            contentDescription = "Battery",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "100%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Settings 2x2 Grid Tiles
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PixelQuickTile(
                            title = "Internet",
                            icon = Icons.Default.Wifi,
                            hasChevron = true,
                            isActive = toggleStates["Internet"] ?: true,
                            onClick = { toggleStates["Internet"] = !(toggleStates["Internet"] ?: true) },
                            modifier = Modifier.weight(1f)
                        )
                        PixelQuickTile(
                            title = "Bluetooth",
                            icon = Icons.Default.Bluetooth,
                            hasChevron = true,
                            isActive = toggleStates["Bluetooth"] ?: false,
                            onClick = { toggleStates["Bluetooth"] = !(toggleStates["Bluetooth"] ?: false) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PixelQuickTile(
                            title = "Flashlight",
                            icon = Icons.Default.FlashlightOn,
                            hasChevron = false,
                            isActive = toggleStates["Flashlight"] ?: false,
                            onClick = { toggleStates["Flashlight"] = !(toggleStates["Flashlight"] ?: false) },
                            modifier = Modifier.weight(1f)
                        )
                        PixelQuickTile(
                            title = "Do Not Disturb",
                            icon = Icons.Default.DoNotDisturb,
                            hasChevron = false,
                            isActive = toggleStates["Do Not Disturb"] ?: false,
                            onClick = { toggleStates["Do Not Disturb"] = !(toggleStates["Do Not Disturb"] ?: false) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Notifications Section
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Security & Privacy Notification Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2023))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2D3035)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Security,
                                                contentDescription = null,
                                                tint = Color(0xFF7CACEA),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Security & privacy • now",
                                            fontSize = 12.sp,
                                            color = Color(0xFF9AA0A6)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Collapse",
                                        tint = Color(0xFF9AA0A6),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Set a screen lock",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "For added security, set a PIN, pattern, or password for this device.",
                                    fontSize = 13.sp,
                                    color = Color(0xFFC4C7C5),
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }

                    // Silent Section Label
                    item {
                        Text(
                            text = "Silent",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9AA0A6),
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 2.dp)
                        )
                    }

                    // Silent Android System Notification Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2023))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF2D3035)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Keyboard,
                                                contentDescription = null,
                                                tint = Color(0xFF7CACEA),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Android System",
                                            fontSize = 12.sp,
                                            color = Color(0xFF9AA0A6)
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "2",
                                            fontSize = 12.sp,
                                            color = Color(0xFF9AA0A6)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Expand",
                                            tint = Color(0xFF9AA0A6),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "qwerty2 configured Keyboard layout set to Engli...",
                                    fontSize = 13.sp,
                                    color = Color(0xFFE2E2E2),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Serial console enabled Performance is impacte...",
                                    fontSize = 13.sp,
                                    color = Color(0xFF9AA0A6),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Actions: Manage & Clear All Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .background(Color(0xFF2D3035))
                            .clickable {
                                onDismiss()
                                onOpenSettings()
                            }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Manage",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .background(Color(0xFF2D3035))
                            .clickable { onDismiss() }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Clear all",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PixelQuickTile(
    title: String,
    icon: ImageVector,
    hasChevron: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isActive) Color(0xFFD3E3FD) else Color(0xFF1E2023)
    val contentColor = if (isActive) Color(0xFF041E49) else Color.White

    Row(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                maxLines = 1
            )
        }

        if (hasChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Expand",
                tint = contentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
