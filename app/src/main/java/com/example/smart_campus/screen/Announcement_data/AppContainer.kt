package com.example.smart_campus.screen.Announcement_data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val announcementRepository: AnnouncementRepository
}

/**
 * [AppContainer] implementation that provides instance of [AnnouncementRepository]
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    override val announcementRepository: AnnouncementRepository by lazy {
        AnnouncementRepository(AnnouncementAppDataBase.getDatabase(context).announcementDao())
    }
}