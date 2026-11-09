package com.example.ui.components

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PixelWeatherData
import com.example.model.WidgetModel
import com.example.model.WidgetType
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PixelWidgetContainer(
    widget: WidgetModel,
    weatherData: PixelWeatherData,
    onRemove: () -> Unit,
    onOpenWeather: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val onDateClick = remember(context) {
        {
            val calendarIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(calendarIntent)
            } catch (e: Exception) {
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("widget_${widget.type.name}")
    ) {
        when (widget.type) {
            WidgetType.AT_A_GLANCE -> {
                AtAGlanceWidget(
                    weatherData = weatherData,
                    onDateClick = onDateClick,
                    onWeatherClick = onOpenWeather
                )
            }
            WidgetType.WEATHER_PILL -> {
                PixelWeatherCardWidget(
                    weatherData = weatherData,
                    onClick = onOpenWeather
                )
            }
            WidgetType.CLOCK_ANALOG -> {
                PixelAnalogClockWidget()
            }
            WidgetType.CLOCK_DIGITAL -> {
                PixelDigitalClockWidget()
            }
        }
    }
}

@Composable
fun PixelWeatherCardWidget(
    weatherData: PixelWeatherData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.88f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = weatherData.city,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = weatherData.condition,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Condition",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${weatherData.temperature}°",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5-day mini forecast bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weatherData.dailyForecast.take(5).forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = day.day,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${day.highTemp}°",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PixelAnalogClockWidget(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        while (true) {
            calendar = Calendar.getInstance()
            delay(1000)
        }
    }

    val onClockClick = remember(context, onClick) {
        onClick ?: {
            val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(clockIntent)
            } catch (e: Exception) {
                try {
                    val fallbackIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(fallbackIntent)
                } catch (e2: Exception) {
                }
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
    val onSurface = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .size(140.dp)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(surfaceColor)
            .clickable(onClick = onClockClick)
            .border(2.dp, primaryColor.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Hour markers
            for (i in 0 until 12) {
                val angle = Math.toRadians((i * 30).toDouble())
                val start = Offset(
                    (center.x + (radius - 12) * sin(angle)).toFloat(),
                    (center.y - (radius - 12) * cos(angle)).toFloat()
                )
                val end = Offset(
                    (center.x + radius * sin(angle)).toFloat(),
                    (center.y - radius * cos(angle)).toFloat()
                )
                drawLine(
                    color = onSurface.copy(alpha = 0.6f),
                    start = start,
                    end = end,
                    strokeWidth = if (i % 3 == 0) 3f else 1.5f,
                    cap = StrokeCap.Round
                )
            }

            val hours = calendar.get(Calendar.HOUR)
            val minutes = calendar.get(Calendar.MINUTE)
            val seconds = calendar.get(Calendar.SECOND)

            // Hour hand
            val hourAngle = Math.toRadians(((hours + minutes / 60f) * 30).toDouble())
            val hourHandEnd = Offset(
                (center.x + (radius * 0.5f) * sin(hourAngle)).toFloat(),
                (center.y - (radius * 0.5f) * cos(hourAngle)).toFloat()
            )
            drawLine(
                color = primaryColor,
                start = center,
                end = hourHandEnd,
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )

            // Minute hand
            val minAngle = Math.toRadians((minutes * 6).toDouble())
            val minHandEnd = Offset(
                (center.x + (radius * 0.75f) * sin(minAngle)).toFloat(),
                (center.y - (radius * 0.75f) * cos(minAngle)).toFloat()
            )
            drawLine(
                color = primaryColor,
                start = center,
                end = minHandEnd,
                strokeWidth = 3.5f,
                cap = StrokeCap.Round
            )

            // Second hand
            val secAngle = Math.toRadians((seconds * 6).toDouble())
            val secHandEnd = Offset(
                (center.x + (radius * 0.85f) * sin(secAngle)).toFloat(),
                (center.y - (radius * 0.85f) * cos(secAngle)).toFloat()
            )
            drawLine(
                color = secondaryColor,
                start = center,
                end = secHandEnd,
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )

            // Center pivot
            drawCircle(color = primaryColor, radius = 4f, center = center)
        }
    }
}

@Composable
fun PixelDigitalClockWidget(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }
    var timeStr by remember { mutableStateOf(timeFormat.format(Date())) }
    var dateStr by remember { mutableStateOf(dateFormat.format(Date())) }
    var alarmOrDayStr by remember { mutableStateOf("") }

    val updateTimeAndAlarm: () -> Unit = {
        val now = Date()
        timeStr = timeFormat.format(now)
        dateStr = dateFormat.format(now)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val nextAlarm = try {
            alarmManager?.nextAlarmClock
        } catch (e: Exception) {
            null
        }
        alarmOrDayStr = if (nextAlarm != null) {
            val alarmDate = Date(nextAlarm.triggerTime)
            val alarmTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            "Alarm: ${alarmTimeFormat.format(alarmDate)}"
        } else {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(now)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            updateTimeAndAlarm()
            delay(1000)
        }
    }

    val onClockClick = remember(context, onClick) {
        onClick ?: {
            val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(clockIntent)
            } catch (e: Exception) {
                try {
                    val fallbackIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(fallbackIntent)
                } catch (e2: Exception) {
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .clickable(onClick = onClockClick),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = timeStr,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = dateStr,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = alarmOrDayStr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
