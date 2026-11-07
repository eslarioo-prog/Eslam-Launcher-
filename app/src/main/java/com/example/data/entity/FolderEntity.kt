package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val pageIndex: Int = 0,
    val cellX: Int = 0,
    val cellY: Int = 0,
    val isDock: Boolean = false,
    val colorTag: String = "primary"
)
