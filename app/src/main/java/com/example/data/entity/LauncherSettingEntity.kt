package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "launcher_settings")
data class LauncherSettingEntity(
    @PrimaryKey
    val key: String,
    val value: String
)
