package com.vaanigoel.vanaspati.profile

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.utils.PrefsManager

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val prefs = PrefsManager(this)
        val savedLanguage = prefs.getLanguage()
        val savedLevel = prefs.getHelpLevel()
        val username = intent.getStringExtra("username") ?: "Vaani"

        val welcomeText = findViewById<TextView>(R.id.welcomeText)

        val headerText = getString(R.string.profile_header_format, username, savedLanguage, savedLevel)
        welcomeText.text = headerText

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}