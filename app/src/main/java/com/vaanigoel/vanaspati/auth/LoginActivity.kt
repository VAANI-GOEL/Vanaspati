package com.vaanigoel.vanaspati.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.profile.ProfileActivity
import com.vaanigoel.vanaspati.utils.PrefsManager
import com.vaanigoel.vanaspati.onboarding.LanguageActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Setup UI and Edge-to-Edge
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // 2. Initialize Views
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Initialize PrefsManager
        val prefs = PrefsManager(this)

        // 3. Handle Login Logic
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Reset errors
            emailLayout.error = null
            passwordLayout.error = null

            // Validation Logic
            var isValid = true

            if (email.isEmpty()) {
                emailLayout.error = "Email is required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Please enter a valid email"
                isValid = false
            }

            if (password.isEmpty()) {
                passwordLayout.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                passwordLayout.error = "Password must be at least 6 characters"
                isValid = false
            }

            // 4. Navigation Logic (Only if Valid)
            if (isValid) {
                Toast.makeText(this, "Logging in to Vanaspati...", Toast.LENGTH_SHORT).show()

                if (prefs.isFirstLogin()) {
                    // Redirect to Onboarding (Language Selection)
                    val intent = Intent(this, LanguageActivity::class.java)
                    startActivity(intent)
                } else {
                    // Already set up, go to Profile
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("username", "Vaani") // You can later replace this with the actual user name
                    startActivity(intent)
                }
                finish() // Optional: Closes LoginActivity so user can't go back to it via back button
            }
        }

        // 5. Apply Window Insets for the layout (Ensures UI doesn't hide under system bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}