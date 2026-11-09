package com.example.model

import androidx.compose.runtime.Immutable

enum class AppCategory(val title: String) {
    ALL("All Apps"),
    SUGGESTED("Suggested"),
    COMMUNICATION("Communication"),
    SOCIAL("Social"),
    PRODUCTIVITY("Productivity"),
    MEDIA("Media & Entertainment"),
    GAMES("Games"),
    GOOGLE("Google & Pixel"),
    UTILITIES("Utilities"),
    SYSTEM("System")
}

@Immutable
data class AppShortcut(
    val id: String,
    val label: String,
    val iconName: String,
    val actionType: String = "launch"
)

@Immutable
data class AppModel(
    val packageName: String,
    val activityName: String = "",
    val label: String,
    val defaultLabel: String = label,
    val iconRes: Int? = null,
    val systemIconKey: String = "android",
    val category: AppCategory = AppCategory.ALL,
    val launchCount: Int = 0,
    val isPinned: Boolean = false,
    val isDock: Boolean = false,
    val folderId: Long? = null,
    val isHidden: Boolean = false,
    val pageIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0,
    val shortcuts: List<AppShortcut> = emptyList()
)

enum class WidgetType(val displayName: String, val description: String, val defaultSpanX: Int, val defaultSpanY: Int) {
    AT_A_GLANCE("At a Glance", "Pixel live weather, calendar events and alerts", 4, 1),
    WEATHER_PILL("Pixel Weather", "Live conditions and 5-day forecast card", 4, 2),
    CLOCK_ANALOG("Analog Clock", "Material You dynamic shape analog clock", 2, 2),
    CLOCK_DIGITAL("Digital Clock", "Modern Pixel numeral typography clock", 2, 2)
}

@Immutable
data class WidgetModel(
    val id: Long = 0,
    val type: WidgetType,
    val pageIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0,
    val spanX: Int = 4,
    val spanY: Int = 2,
    val configData: String = ""
)

@Immutable
data class FolderModel(
    val id: Long = 0,
    val title: String,
    val pageIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0,
    val isDock: Boolean = false,
    val apps: List<AppModel> = emptyList(),
    val colorTag: String = "primary"
)

@Immutable
data class DiscoverNewsItem(
    val id: String,
    val title: String,
    val publisher: String,
    val timeAgo: String,
    val category: String,
    val summary: String,
    val articleUrl: String,
    val likes: Int = 120,
    val isBookmarked: Boolean = false,
    val drawableRes: Int? = null
)

@Immutable
data class HourlyForecast(
    val time: String,
    val temp: Int,
    val iconKey: String
)

@Immutable
data class DailyForecast(
    val day: String,
    val condition: String,
    val highTemp: Int,
    val lowTemp: Int,
    val iconKey: String
)

@Immutable
data class PixelWeatherData(
    val temperature: Int = 21,
    val condition: String = "Mostly Sunny",
    val conditionCode: String = "sunny",
    val city: String = "Mountain View, CA",
    val highTemp: Int = 24,
    val lowTemp: Int = 14,
    val humidity: Int = 46,
    val uvIndex: Int = 5,
    val windSpeed: String = "11 km/h",
    val airQuality: String = "AQI 32 • Good",
    val hourlyForecast: List<HourlyForecast> = listOf(
        HourlyForecast("Now", 21, "sunny"),
        HourlyForecast("11 AM", 22, "sunny"),
        HourlyForecast("12 PM", 23, "sunny"),
        HourlyForecast("1 PM", 24, "partly_cloudy"),
        HourlyForecast("2 PM", 24, "partly_cloudy"),
        HourlyForecast("3 PM", 23, "cloudy"),
        HourlyForecast("4 PM", 22, "cloudy"),
        HourlyForecast("5 PM", 20, "rain"),
        HourlyForecast("6 PM", 19, "rain")
    ),
    val dailyForecast: List<DailyForecast> = listOf(
        DailyForecast("Today", "Mostly Sunny", 24, 14, "sunny"),
        DailyForecast("Tue", "Partly Cloudy", 23, 13, "partly_cloudy"),
        DailyForecast("Wed", "Scattered Showers", 19, 12, "rain"),
        DailyForecast("Thu", "Sunny & Clear", 22, 11, "sunny"),
        DailyForecast("Fri", "Pleasant Breeze", 25, 15, "sunny")
    )
)

enum class PixelIconShape(val id: String, val displayName: String) {
    SQUIRCLE("SQUIRCLE", "Squircle"),
    ROUNDED_SQUARE("ROUNDED_SQUARE", "Rounded Square"),
    CIRCLE("CIRCLE", "Circle"),
    TEARDROP("TEARDROP", "Teardrop")
}

@Immutable
data class LauncherConfig(
    val wallpaperId: String = "obsidian", // obsidian, meadow, sunset, dynamic_aurora
    val colorPaletteName: String = "Obsidian Slate", // Obsidian Slate, Bay Cyan, Meadow Mint, Coral Rose, Lilac Lavender
    val useThemedIcons: Boolean = false,
    val iconShape: String = "SQUIRCLE", // SQUIRCLE, ROUNDED_SQUARE, CIRCLE, TEARDROP
    val dockStyle: String = "PILL_BLUR", // TRANSPARENT, PILL_BLUR, SOLID_CARD
    val doubleTapToSleep: Boolean = true,
    val swipeDownGesture: String = "NOTIFICATIONS", // NOTIFICATIONS, APP_DRAWER, NONE
    val searchEngine: String = "GOOGLE", // GOOGLE, GEMINI_LIVE
    val gridRows: Int = 5,
    val gridCols: Int = 4,
    val showDockSearch: Boolean = true,
    val showAtAGlanceWeather: Boolean = true,
    val showAtAGlanceCalendar: Boolean = true,
    val showAtAGlanceBattery: Boolean = true,
    val darkModePreference: String = "SYSTEM", // SYSTEM, DARK, LIGHT
    val iconScale: Float = 1.0f
)
