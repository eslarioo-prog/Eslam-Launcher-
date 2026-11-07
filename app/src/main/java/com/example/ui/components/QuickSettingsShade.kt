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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeStr = remember { timeFormat.format(Date()) }
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }
    val dateStr = remember { dateFormat.format(Date()) }

    var brightness by remember { mutableFloatStateOf(0.75f) }

    val toggleStates = remember {
        mutableStateMapOf(
            "Internet" to true,
            "Bluetooth" to true,
            "Flashlight" to false,
            "Do Not Disturb" to false,
            "Dark Theme" to true,
            "Auto-rotate" to true
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF0101418))
                .statusBarsPadding()
                .padding(16.dp)
                .testTag("quick_settings_shade")
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Bar with Clock and Action icons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = timeStr,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = dateStr,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    onDismiss()
                                    onOpenSettings()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = "Power",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // Brightness Slider
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF242A30))
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrightnessMedium,
                            contentDescription = "Brightness",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Slider(
                            value = brightness,
                            onValueChange = { brightness = it },
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                // 2x3 Quick Setting Tile Pills
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickSettingTile(
                                title = "Internet",
                                subtitle = "Pixel_5G_Fast",
                                icon = Icons.Default.Wifi,
                                isActive = toggleStates["Internet"] ?: true,
                                onClick = { toggleStates["Internet"] = !(toggleStates["Internet"] ?: true) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickSettingTile(
                                title = "Bluetooth",
                                subtitle = "Buds Pro 2",
                                icon = Icons.Default.Bluetooth,
                                isActive = toggleStates["Bluetooth"] ?: true,
                                onClick = { toggleStates["Bluetooth"] = !(toggleStates["Bluetooth"] ?: true) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickSettingTile(
                                title = "Flashlight",
                                subtitle = if (toggleStates["Flashlight"] == true) "On" else "Off",
                                icon = Icons.Default.FlashlightOn,
                                isActive = toggleStates["Flashlight"] ?: false,
                                onClick = { toggleStates["Flashlight"] = !(toggleStates["Flashlight"] ?: false) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickSettingTile(
                                title = "Do Not Disturb",
                                subtitle = if (toggleStates["Do Not Disturb"] == true) "On" else "Off",
                                icon = Icons.Default.DoNotDisturb,
                                isActive = toggleStates["Do Not Disturb"] ?: false,
                                onClick = { toggleStates["Do Not Disturb"] = !(toggleStates["Do Not Disturb"] ?: false) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            QuickSettingTile(
                                title = "Dark Theme",
                                subtitle = "Scheduled",
                                icon = Icons.Default.DarkMode,
                                isActive = toggleStates["Dark Theme"] ?: true,
                                onClick = { toggleStates["Dark Theme"] = !(toggleStates["Dark Theme"] ?: true) },
                                modifier = Modifier.weight(1f)
                            )
                            QuickSettingTile(
                                title = "Auto-rotate",
                                subtitle = "On",
                                icon = Icons.Default.ScreenRotation,
                                isActive = toggleStates["Auto-rotate"] ?: true,
                                onClick = { toggleStates["Auto-rotate"] = !(toggleStates["Auto-rotate"] ?: true) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Notification Section Header
                item {
                    Text(
                        text = "Notifications",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Notification Card 1: Google Calendar
                item {
                    NotificationItemCard(
                        appName = "Calendar",
                        title = "Upcoming: Team Sync in 15m",
                        desc = "Google Meet link ready • 4 attendees",
                        timeAgo = "15m"
                    )
                }

                // Notification Card 2: Messages
                item {
                    NotificationItemCard(
                        appName = "Messages",
                        title = "Alex Rivera",
                        desc = "Hey, are you trying out the new Pixel 10 launcher? Looks clean!",
                        timeAgo = "32m"
                    )
                }

                // Notification Card 3: Pixel Tips
                item {
                    NotificationItemCard(
                        appName = "Pixel Tips",
                        title = "Magic Compose is ready",
                        desc = "Use Gemini on-device to rewrite drafts in your favorite style.",
                        timeAgo = "1h"
                    )
                }
            }
        }
    }
}

@Composable
fun QuickSettingTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBg = MaterialTheme.colorScheme.primary
    val inactiveBg = Color(0xFF242A30)
    val activeContent = MaterialTheme.colorScheme.onPrimary
    val inactiveContent = Color.White

    Row(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isActive) activeBg else inactiveBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isActive) activeContent else inactiveContent,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) activeContent else inactiveContent,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = if (isActive) activeContent.copy(alpha = 0.8f) else inactiveContent.copy(alpha = 0.6f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun NotificationItemCard(
    appName: String,
    title: String,
    desc: String,
    timeAgo: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF20262C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = appName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = timeAgo,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}
