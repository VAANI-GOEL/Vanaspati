package com.vaanigoel.vanaspati.utils

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("vanaspati_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_UID       = "uid"
        private const val KEY_EMAIL     = "email"
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_LOGGED_IN, false)
    }

    fun saveUser(uid: String, email: String) {
        prefs.edit()
            .putString(KEY_UID, uid)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getUid(): String   = prefs.getString(KEY_UID, "")   ?: ""
    fun getEmail(): String = prefs.getString(KEY_EMAIL, "") ?: ""

    fun logout() {
        prefs.edit().clear().apply()
    }
}