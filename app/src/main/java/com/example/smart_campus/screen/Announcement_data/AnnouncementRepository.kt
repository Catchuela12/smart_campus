package com.example.smart_campus.screen.Announcement_data

import kotlinx.coroutines.flow.Flow

class AnnouncementRepository(private val announcementDao: AnnouncementDao) {

    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()

    suspend fun insert(announcement: Announcement) {
        announcementDao.insertAnnouncement(announcement)
    }

    suspend fun update(announcement: Announcement) {
        announcementDao.updateAnnouncement(announcement)
    }

    // ← NEW: required for admin delete
    suspend fun delete(announcement: Announcement) {
        announcementDao.deleteAnnouncement(announcement)
    }

    suspend fun getAnnouncementById(id: Int): Announcement? {
        return announcementDao.getAnnouncementById(id)
    }
}