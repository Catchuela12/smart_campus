package com.example.smart_campus.screen.Announcement_viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.smart_campus.screen.Announcement_data.AnnouncementRepository
import com.example.smart_campus.screen.Announcement_data.Announcement
import com.example.smart_campus.screen.Announcement_data.AnnouncementApp

class AnnouncementViewModel(private val repository: AnnouncementRepository) : ViewModel() {

    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        addSampleData()
    }

    fun toggleReadStatus(announcement: Announcement) {
        viewModelScope.launch {
            repository.update(announcement.copy(isRead = !announcement.isRead))
        }
    }

    fun markAsRead(announcement: Announcement) {
        viewModelScope.launch {
            if (!announcement.isRead) {
                repository.update(announcement.copy(isRead = true))
            }
        }
    }

    private fun addSampleData() {
        viewModelScope.launch {
            val currentList = repository.allAnnouncements.first()
            val samples = listOf(
                Announcement(
                    title = "Midterm Examination Schedule",
                    content = "The official midterm examination schedule for the Second Semester, Academic Year 2025-2026 is now released. Students are advised to check the portal for their respective schedules and assigned rooms. Please ensure you have your valid ID and permit.",
                    date = "March 20, 2026",
                    iconName = "Event",
                    categoryColor = 0xFF1565C0
                ),
                Announcement(
                    title = "University Library: Extended Operating Hours",
                    content = "In preparation for the upcoming examinations, the University Library will extend its operating hours. Starting next Monday, the library will be open from 7:00 AM until 12:00 midnight, Monday through Saturday. Weekend study sessions are encouraged.",
                    date = "March 20, 2026",
                    iconName = "LibraryBooks",
                    categoryColor = 0xFF2E7D32
                ),
                Announcement(
                    title = "Annual Career and Internship Fair 2026",
                    content = "Join us for the Annual Career and Internship Fair on November 15th at the University Grand Hall. Meet with over 60 industry partners looking for talented students for internships and career opportunities. Pre-register through the student portal.",
                    date = "March 20, 2026",
                    iconName = "Work",
                    categoryColor = 0xFFE65100
                ),
                Announcement(
                    title = "Merit Scholarship Applications for AY 2025-2026",
                    content = "The Office of Admissions and Scholarships is now accepting applications for the Merit Scholarship for the upcoming Academic Year. Applicants must maintain a GWA of 1.75 or higher. Deadline for submission of requirements is on November 30th.",
                    date = "March 20, 2026",
                    iconName = "School",
                    categoryColor = 0xFF6A1B9A
                ),
                Announcement(
                    title = "Campus-wide Infrastructure Maintenance",
                    content = "The Facilities Management Office will conduct a campus-wide electrical maintenance this coming Sunday, Oct 29. Expect intermittent power outages in various buildings from 8:00 AM to 5:00 PM. Please save your work and power down sensitive equipment.",
                    date = "March 20, 2026",
                    iconName = "Build",
                    categoryColor = 0xFFC62828
                )
            )

            samples.forEach { sample ->
                if (currentList.none { it.title == sample.title }) {
                    repository.insert(sample)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AnnouncementApp

                // Create a new instance of the AnnouncementViewModel
                return AnnouncementViewModel(
                    application.container.announcementRepository
                ) as T
            }
        }
    }
}
