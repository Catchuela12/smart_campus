package com.example.smart_campus.screen

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * App-wide dark mode state.
 *
 * Defaults to TRUE for all users.
 */
object AppThemeState {

    private const val PREFS_NAME = "smart_campus_theme_prefs"
    private const val KEY_DARK   = "is_dark_mode"

    // Observable — Smart_campusTheme reads this to pick the color scheme.
    // Defaulting to true for app-wide dark mode.
    var isDarkMode by mutableStateOf(true)
        private set

    /** Call in every Activity.onCreate() before setContent { } */
    fun init(context: Context) {
        val prefs  = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Default to true if the key hasn't been set yet
        isDarkMode = prefs.getBoolean(KEY_DARK, true)
    }

    /**
     * Toggle dark mode, persist the preference, then recreate the Activity
     * so every hardcoded color and composable rebuilds with the new theme.
     */
    fun setDarkMode(context: Context, enabled: Boolean) {
        isDarkMode = enabled
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK, enabled)
            .apply()

        if (context is Activity) {
            context.recreate()
        }
    }
}