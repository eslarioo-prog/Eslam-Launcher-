package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_widgets")
data class WidgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val widgetType: String, // AT_A_GLANCE, WEATHER_PILL, CLOCK_ANALOG, CLOCK_DIGITAL, BATTERY, MUSIC, WELLBEING
    val pageIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0,
    val spanX: Int = 4,
    val spanY: Int = 2,
    val configData: String = ""
)
