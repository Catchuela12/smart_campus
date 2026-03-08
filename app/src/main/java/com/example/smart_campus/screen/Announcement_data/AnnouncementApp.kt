package com.example.smart_campus.screen.Announcement_data

import android.app.Application

/**
 * Application class that initializes the [DefaultAppContainer]
 */
class AnnouncementApp : Application() {

    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}