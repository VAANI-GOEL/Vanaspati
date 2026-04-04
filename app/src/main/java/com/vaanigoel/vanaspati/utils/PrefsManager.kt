package com.vaanigoel.vanaspati.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit // This makes the warnings go away

class PrefsManager(context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("VanaspatiPrefs", Context.MODE_PRIVATE)

    // Check if user has done the first-time setup
    fun setFirstLogin(isFirst: Boolean) {
        sharedPref.edit { putBoolean("is_first_login", isFirst) }
    }

    fun isFirstLogin(): Boolean = sharedPref.getBoolean("is_first_login", true)

    // Save/Get the selected Indian Language
    fun saveLanguage(language: String) {
        sharedPref.edit { putString("selected_language", language) }
    }

    fun getLanguage(): String = sharedPref.getString("selected_language", "English") ?: "English"

    // Save/Get Gardening Help Level
    fun saveHelpLevel(level: String) {
        sharedPref.edit { putString("help_level", level) }
    }

    fun getHelpLevel(): String = sharedPref.getString("help_level", "Beginner") ?: "Beginner"
}