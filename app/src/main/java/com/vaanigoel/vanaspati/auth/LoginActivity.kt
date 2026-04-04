package com.vaanigoel.vanaspati.auth
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.profile.ProfileActivity
import android.widget.Button
import com.vaanigoel.vanaspati.utils.PrefsManager


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val loginButton = findViewById<Button>(R.id.loginButton)

        val prefs = PrefsManager(this)

        loginButton.setOnClickListener {
            if (prefs.isFirstLogin()) {
                // Redirect to Onboarding (Language Selection)
                // Note: You'll need to create LanguageActivity in your onboarding package
                val intent = Intent(this, com.vaanigoel.vanaspati.onboarding.LanguageActivity::class.java)
                startActivity(intent)
            } else {
                // Already set up, go to Profile as you currently do
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("username", "Vaani")
                startActivity(intent)
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}