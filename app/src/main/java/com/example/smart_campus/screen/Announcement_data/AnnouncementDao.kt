package com.example.smart_campus.screen.Announcement_data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {

    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Update
    suspend fun updateAnnouncement(announcement: Announcement)

    @Query("SELECT * FROM announcements WHERE id = :id")
    suspend fun getAnnouncementById(id: Int): Announcement?
}