package com.vaanigoel.vanaspati.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.profile.ProfileActivity
import com.vaanigoel.vanaspati.utils.PrefsManager

class HelpLevelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_level)

        val prefs = PrefsManager(this)

        val btnBeginner = findViewById<Button>(R.id.btnBeginner)
        val btnIntermediate = findViewById<Button>(R.id.btnIntermediate)
        val btnConfident = findViewById<Button>(R.id.btnConfident)

        btnBeginner.setOnClickListener { finishSetup(prefs, "Beginner") }
        btnIntermediate.setOnClickListener { finishSetup(prefs, "Intermediate") }
        btnConfident.setOnClickListener { finishSetup(prefs, "Confident") }
    }

    private fun finishSetup(prefs: PrefsManager, level: String) {
        // 1. Save the chosen level
        prefs.saveHelpLevel(level)

        // 2. Mark setup as COMPLETE so they don't see this again
        prefs.setFirstLogin(false)

        // 3. Move to the Profile
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("username", "Vaani") // Using your default name logic
        startActivity(intent)
        finish()
    }
}