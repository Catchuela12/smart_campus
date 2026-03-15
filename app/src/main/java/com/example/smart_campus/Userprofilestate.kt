package com.example.smart_campus.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserProfileState {

    private const val PREFS_NAME = "smart_campus_profile_prefs"
    private const val KEY_PHOTO  = "profile_photo_uri"
    private const val KEY_NAME   = "display_name"

    private var currentUserId: Int = -1

    var profileImageUri by mutableStateOf<Uri?>(null)
        private set

    var displayName by mutableStateOf("")
        private set

    /**
     * Always reloads from SharedPreferences on every onCreate call.
     * Verifies the URI still has a valid persistable permission before using it.
     */
    fun init(context: Context, userId: Int) {
        currentUserId = userId

        val prefs     = prefs(context)
        val savedUri  = prefs.getString(photoKey(userId), null)
        val savedName = prefs.getString(nameKey(userId), null)

        // Check the URI is still readable via persisted permissions
        profileImageUri = if (savedUri != null) {
            val uri = Uri.parse(savedUri)
            val hasPermission = context.contentResolver
                .persistedUriPermissions
                .any { it.uri == uri && it.isReadPermission }
            if (hasPermission) uri else null
        } else null

        displayName = savedName ?: ""
    }

    /** Save photo URI — caller must have already called takePersistableUriPermission. */
    fun saveProfileImage(context: Context, uri: Uri) {
        require(currentUserId != -1) { "Call init() before saving." }
        profileImageUri = uri
        prefs(context).edit()
            .putString(photoKey(currentUserId), uri.toString())
            .apply()
    }

    fun saveDisplayName(context: Context, name: String) {
        require(currentUserId != -1) { "Call init() before saving." }
        displayName = name
        prefs(context).edit()
            .putString(nameKey(currentUserId), name)
            .apply()
    }

    fun seedDisplayNameIfEmpty(context: Context, fallbackName: String) {
        if (displayName.isBlank()) {
            saveDisplayName(context, fallbackName)
        }
    }

    fun clear() {
        currentUserId   = -1
        profileImageUri = null
        displayName     = ""
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun photoKey(userId: Int) = "${KEY_PHOTO}_$userId"
    private fun nameKey(userId: Int)  = "${KEY_NAME}_$userId"
}