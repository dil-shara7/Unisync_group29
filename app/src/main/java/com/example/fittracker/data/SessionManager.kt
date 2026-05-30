package com.example.fittracker.data

import android.content.Context
import androidx.core.content.edit

/**
 * Stores the currently logged-in user id in SharedPreferences so screens can
 * scope queries by ownerId without re-authenticating each launch.
 */
class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var currentUserId: Long
        get() = prefs.getLong(KEY_USER_ID, NO_USER)
        set(value) = prefs.edit { putLong(KEY_USER_ID, value) }

    val isLoggedIn: Boolean
        get() = currentUserId != NO_USER

    fun clear() = prefs.edit { remove(KEY_USER_ID) }

    companion object {
        const val NO_USER: Long = -1L
        private const val PREFS_NAME = "unisync_session"
        private const val KEY_USER_ID = "current_user_id"
    }
}
