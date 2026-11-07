package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PixelWeatherData
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnimatedWeatherIcon(
    conditionCode: String = "sunny",
    iconTint: Color = Color(0xFFFFD54F),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather_anim")

    when {
        "rain" in conditionCode.lowercase() || "shower" in conditionCode.lowercase() -> {
            val translateY by infiniteTransition.animateFloat(
                initialValue = -1.5f,
                targetValue = 2.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "rain_drop"
            )
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = "Rain",
                tint = Color(0xFF81D4FA),
                modifier = modifier
                    .size(18.dp)
                    .graphicsLayer { translationY = translateY.dp.toPx() }
            )
        }
        "cloud" in conditionCode.lowercase() -> {
            val translateX by infiniteTransition.animateFloat(
                initialValue = -2.0f,
                targetValue = 2.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "cloud_float"
            )
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = "Cloudy",
                tint = Color(0xFFECEFF1),
                modifier = modifier
                    .size(18.dp)
                    .graphicsLayer { translationX = translateX.dp.toPx() }
            )
        }
        else -> { // Sunny / Clear / Mostly Sunny
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(12000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "sun_rotate"
            )
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "sun_pulse"
            )
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Sunny",
                tint = iconTint,
                modifier = modifier
                    .size(18.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }
    }
}

/**
 * Reusable Google Pixel 'At a Glance' Composable.
 * Uses Material 3 typography and clean styling with optional text shadow for readability over wallpapers.
 *
 * @param dateText Formatted date string. Defaults to current system date (e.g. "Tuesday, Oct 6").
 * @param temperature Temperature label (e.g. "21°C").
 * @param condition Weather condition description (e.g. "Mostly Sunny").
 * @param conditionCode Weather condition code string (e.g. "sunny", "rain", "partly_cloudy").
 * @param weatherIcon Icon representing the weather condition.
 * @param weatherIconTint Color tint for the weather icon.
 * @param smartSubtitle Optional contextual event or alert text (e.g. "10:30 AM • Sync with Product Team").
 * @param subtitleIcon Optional icon accompanying the subtitle.
 * @param showWeather Whether to display the weather temperature and icon pill.
 * @param useTextShadow Whether to apply a subtle text shadow for wallpaper contrast.
 * @param onDateClick Callback triggered when the date is tapped.
 * @param onWeatherClick Callback triggered when the weather pill is tapped.
 * @param onSubtitleClick Callback triggered when the smart alert pill is tapped.
 */
@Composable
fun AtAGlance(
    modifier: Modifier = Modifier,
    dateText: String = remember { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date()) },
    temperature: String = "21°C",
    condition: String = "Mostly Sunny",
    conditionCode: String = "sunny",
    weatherIcon: ImageVector = Icons.Default.WbSunny,
    weatherIconTint: Color = Color(0xFFFFD54F),
    smartSubtitle: String? = "10:30 AM • Sync with Product Team",
    subtitleIcon: ImageVector? = Icons.Default.CalendarMonth,
    showWeather: Boolean = true,
    useTextShadow: Boolean = true,
    onDateClick: () -> Unit = {},
    onWeatherClick: () -> Unit = {},
    onSubtitleClick: () -> Unit = {}
) {
    val textShadow = if (useTextShadow) {
        Shadow(
            color = Color.Black.copy(alpha = 0.85f),
            offset = Offset(0f, 2f),
            blurRadius = 6f
        )
    } else {
        null
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .testTag("at_a_glance_composable"),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Main Date & Weather Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date Label using M3 headlineSmall typography
            Text(
                text = dateText,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    shadow = textShadow
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onDateClick)
                    .padding(vertical = 2.dp, horizontal = 4.dp)
                    .testTag("at_a_glance_date_label")
            )

            if (showWeather) {
                Spacer(modifier = Modifier.width(10.dp))

                // Weather Condition Pill using M3 titleMedium typography
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .clickable(onClick = onWeatherClick)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("at_a_glance_weather_pill")
                ) {
                    AnimatedWeatherIcon(
                        conditionCode = conditionCode,
                        iconTint = weatherIconTint
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = temperature,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            shadow = textShadow
                        )
                    )
                }
            }
        }

        // Contextual Smart Alert Pill (Calendar, Pixel Buds, Weather Alerts)
        if (!smartSubtitle.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable(onClick = onSubtitleClick)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("at_a_glance_smart_pill"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (subtitleIcon != null) {
                    Icon(
                        imageVector = subtitleIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = smartSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = Color.White
                    )
                )
            }
        }
    }
}

/**
 * Connected launcher widget with animated cycling smart snippets and PixelWeatherData binding.
 */
@Composable
fun AtAGlanceWidget(
    weatherData: PixelWeatherData,
    showWeather: Boolean = true,
    showCalendar: Boolean = true,
    showBattery: Boolean = true,
    onDateClick: () -> Unit = {},
    onWeatherClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d", Locale.getDefault()) }
    val currentDateStr = remember { dateFormat.format(Date()) }

    // Dynamic cycling smart event snippets
    var currentChipIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentChipIndex = (currentChipIndex + 1) % 3
        }
    }

    val (currentSubtitle, currentIcon) = when (currentChipIndex) {
        0 -> "10:30 AM • Sync with Product Team" to Icons.Default.CalendarMonth
        1 -> "Pixel Buds Pro 2 • 92% Charged" to Icons.Default.Headphones
        else -> "Air Quality 32 • Good in Mountain View" to Icons.Default.Air
    }

    val shouldShowSubtitle = (showCalendar && currentChipIndex == 0) || (showBattery && currentChipIndex == 1) || (showWeather && currentChipIndex == 2)

    AtAGlance(
        modifier = modifier,
        dateText = currentDateStr,
        temperature = "${weatherData.temperature}°C",
        condition = weatherData.condition,
        conditionCode = weatherData.conditionCode,
        weatherIcon = Icons.Default.WbSunny,
        weatherIconTint = Color(0xFFFFD54F),
        smartSubtitle = if (shouldShowSubtitle) currentSubtitle else null,
        subtitleIcon = if (shouldShowSubtitle) currentIcon else null,
        showWeather = showWeather,
        useTextShadow = true,
        onDateClick = onDateClick,
        onWeatherClick = onWeatherClick,
        onSubtitleClick = {
            if (currentChipIndex == 0) onDateClick() else onWeatherClick()
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12161A)
@Composable
fun AtAGlancePreview() {
    MaterialTheme {
        Surface(color = Color(0xFF1B2124)) {
            AtAGlance(
                dateText = "Monday, Oct 6",
                temperature = "21°C",
                condition = "Mostly Sunny",
                smartSubtitle = "10:30 AM • Sync with Product Team"
            )
        }
    }
}
