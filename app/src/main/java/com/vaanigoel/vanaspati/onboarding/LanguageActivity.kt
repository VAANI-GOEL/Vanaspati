package com.vaanigoel.vanaspati.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.utils.PrefsManager

class LanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)
        val btnHindi = findViewById<Button>(R.id.btnHindi)
        val btnEnglish = findViewById<Button>(R.id.btnEnglish)
        val prefs = PrefsManager(this)
        btnHindi.setOnClickListener {
            prefs.saveLanguage("Hindi")
            prefs.saveLanguage("Hindi")
            goToHelpLevel()
        }

        btnEnglish.setOnClickListener {
            prefs.saveLanguage("English")
            prefs.saveLanguage("English")
            goToHelpLevel()
        }
    }

    private fun goToHelpLevel() {
        // This moves the user to the next setup screen
        // Ensure you create HelpLevelActivity in your onboarding package next
        val intent = Intent(this, HelpLevelActivity::class.java)
        startActivity(intent)
        finish()
    }
}