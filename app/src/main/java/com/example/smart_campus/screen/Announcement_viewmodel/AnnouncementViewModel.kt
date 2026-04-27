package com.example.smart_campus.screen.Announcement_viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.smart_campus.screen.Announcement_data.AnnouncementRepository
import com.example.smart_campus.screen.Announcement_data.Announcement
import com.example.smart_campus.fcm.FCMNotificationSender
import com.google.firebase.messaging.FirebaseMessaging

class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {

    init {
        // Automatically subscribe all users to the announcements topic
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("announcements")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun markAsRead(announcement: Announcement) {
        viewModelScope.launch {
            repository.update(announcement.copy(isRead = true))
        }
    }

    // ── Admin-side: post new announcement ────────────────────────────────────

    fun addAnnouncement(context: Context, announcement: Announcement) {
        viewModelScope.launch {
            // 1. Save to local database
            repository.insert(announcement)
            
            // 2. Trigger FCM V1 Notification using the project ID from your json
            FCMNotificationSender.sendAnnouncementNotification(
                context = context,
                projectId = "smart-campus-e6ea1",
                title = announcement.title,
                body = announcement.content
            )
        }
    }

    fun addSampleData() {
        viewModelScope.launch {
            val samples = listOf(
                Announcement(
                    title = "Welcome to Smart Campus",
                    content = "We are excited to have you here. Explore the features of our new app!",
                    date = "Oct 24, 2024"
                ),
                Announcement(
                    title = "Library Hours Updated",
                    content = "The central library will now be open until 10 PM on weekdays starting next Monday.",
                    date = "Oct 25, 2024"
                ),
                Announcement(
                    title = "Upcoming Career Fair",
                    content = "Don't miss the annual career fair this Friday in the main auditorium. Over 50 companies attending.",
                    date = "Oct 26, 2024"
                )
            )
            samples.forEach { repository.insert(it) }
        }
    }

    fun editAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            repository.update(announcement)
        }
    }

    fun deleteAnnouncement(announcement: Announcement) {
        viewModelScope.launch {
            repository.delete(announcement)
        }
    }
}

class AnnouncementViewModelFactory(
    private val repository: AnnouncementRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnouncementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}