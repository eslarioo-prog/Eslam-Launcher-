package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_app_items")
data class AppItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val activityName: String = "",
    val customLabel: String? = null,
    val defaultLabel: String,
    val pageIndex: Int = 0, // 0 = main desktop, 1 = page 2, -1 = dock
    val cellX: Int = 0,
    val cellY: Int = 0,
    val isDock: Boolean = false,
    val folderId: Long? = null,
    val isHidden: Boolean = false,
    val isPinned: Boolean = true,
    val launchCount: Int = 0,
    val lastLaunchedTimestamp: Long = 0L,
    val iconKey: String = ""
)
