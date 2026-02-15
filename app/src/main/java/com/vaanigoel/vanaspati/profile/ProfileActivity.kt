package com.vaanigoel.vanaspati.profile

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // Add this import
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.utils.CareContentGenerator
import com.vaanigoel.vanaspati.utils.PrefsManager
import kotlinx.coroutines.launch // Add this import

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // 1. Initialize Memory
        val prefs = PrefsManager(this)
        val careGenerator = CareContentGenerator(prefs)

        // 2. Find Views
        val tipTextView = findViewById<TextView>(R.id.tipTextView)
        val welcomeText = findViewById<TextView>(R.id.welcomeText)

        // 3. Set static user info
        val username = intent.getStringExtra("username") ?: "Vaani"
        welcomeText.text = getString(R.string.welcome_header, username, prefs.getLanguage(), prefs.getHelpLevel())

        // 4. GENERATIVE AI CALL (Branch 3)
        // This runs in the background so the app doesn't freeze
        lifecycleScope.launch {
            tipTextView.text = getString(R.string.ai_thinking)

            // This calls your Gemini logic in CareContentGenerator
//            val aiTip = careGenerator.generateAiTip("Tulsi")

//            tipTextView.text = aiTip
        }
    }
}