package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.LauncherDao
import com.example.data.entity.AppItemEntity
import com.example.data.entity.FolderEntity
import com.example.data.entity.LauncherSettingEntity
import com.example.data.entity.WidgetEntity

@Database(
    entities = [
        AppItemEntity::class,
        FolderEntity::class,
        WidgetEntity::class,
        LauncherSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LauncherDatabase : RoomDatabase() {

    abstract fun launcherDao(): LauncherDao

    companion object {
        @Volatile
        private var INSTANCE: LauncherDatabase? = null

        fun getDatabase(context: Context): LauncherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LauncherDatabase::class.java,
                    "pixel_launcher_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
