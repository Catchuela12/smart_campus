package com.example.smart_campus.screen

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * App-wide dark mode state.
 *
 * Because many screens use hardcoded Color() values inside non-composable
 * objects (AppColors, ProfileColors, etc.), simply toggling a mutableStateOf
 * is not enough — those values are computed once and never recomposed.
 *
 * The solution: recreate the Activity when the theme changes.
 * This forces ALL composables in the activity to rebuild from scratch,
 * picking up the correct theme-aware colors from SmartCampusColors.current.
 */
object AppThemeState {

    private const val PREFS_NAME = "smart_campus_theme_prefs"
    private const val KEY_DARK   = "is_dark_mode"

    // Observable — Smart_campusTheme reads this to pick the color scheme
    var isDarkMode by mutableStateOf(false)
        private set

    /** Call in every Activity.onCreate() before setContent { } */
    fun init(context: Context) {
        val prefs  = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkMode = prefs.getBoolean(KEY_DARK, false)
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

        // Recreate the host Activity — this is the key fix.
        // All composables rebuild, SmartCampusColors.current returns new values.
        if (context is Activity) {
            context.recreate()
        }
    }
}