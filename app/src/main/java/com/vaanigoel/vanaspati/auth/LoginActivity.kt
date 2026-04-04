package com.vaanigoel.vanaspati.auth
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vaanigoel.vanaspati.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Views
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

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

            if (isValid) {
                Toast.makeText(this, "Logging in to Vanaspati...", Toast.LENGTH_SHORT).show()
                // Proceed to next screen
            }
        }
    }
}