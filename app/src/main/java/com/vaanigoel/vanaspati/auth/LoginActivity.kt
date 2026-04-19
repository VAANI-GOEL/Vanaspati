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
import com.vaanigoel.vanaspati.AddPlantActivity
import com.vaanigoel.vanaspati.HomeDashboard
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.utils.PrefsManager

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge UI
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Views
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Prefs
        val prefs = PrefsManager(this)

        // Login Button Click
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Reset errors
            emailLayout.error = null
            passwordLayout.error = null

            var isValid = true

            // Email validation
            if (email.isEmpty()) {
                emailLayout.error = "Email is required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Enter a valid email"
                isValid = false
            }

            // Password validation
            if (password.isEmpty()) {
                passwordLayout.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                passwordLayout.error = "Minimum 6 characters required"
                isValid = false
            }

            // If valid → Login success
            if (isValid) {
                Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()


                // Move to next screen
                val intent = Intent(this, HomeDashboard::class.java)
                intent.putExtra("username", "Vaani")
                startActivity(intent)

                finish() // close login screen
            }
        }

        // Handle system bars (padding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}