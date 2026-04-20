package com.vaanigoel.vanaspati.utils

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("vanaspati_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LOGGED_IN = "logged_in"
    }

    // Save login state
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, isLoggedIn)
            .apply()
    }

    // Check login state
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }

    // Logout
    fun logout() {
        prefs.edit()
            .clear()
            .apply()
    }
}