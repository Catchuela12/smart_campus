package com.example.smart_campus.screen.Announcement_data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Announcement::class], version = 2, exportSchema = false)
abstract class AnnouncementAppDataBase : RoomDatabase() {

    abstract fun announcementDao(): AnnouncementDao

    companion object {
        @Volatile
        private var INSTANCE: AnnouncementAppDataBase? = null

        fun getDatabase(context: Context): AnnouncementAppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnnouncementAppDataBase::class.java,
                    "announcement_database"
                )
                    .fallbackToDestructiveMigration() // Simplest for development when schema changes
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
