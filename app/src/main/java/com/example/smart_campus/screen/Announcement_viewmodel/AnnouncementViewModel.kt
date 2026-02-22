package com.example.smart_campus.screen.Announcement_viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.smart_campus.screen.Announcement_data.AnnouncementRepository
import com.example.smart_campus.screen.Announcement_data.Announcement

class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {

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

    fun addSampleData() {
        viewModelScope.launch {
            repository.insert(Announcement(title = "Welcome to Campus Announcements!", content = "This is a sample announcement to get you started.", date = "2024-01-01"))
            repository.insert(Announcement(title = "Upcoming Holiday", content = "The campus will be closed for the upcoming holiday. Please plan accordingly.", date = "2024-01-15"))
        }
    }
}

class AnnouncementViewModelFactory(private val repository: AnnouncementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnouncementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
