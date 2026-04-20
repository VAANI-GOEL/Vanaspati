package com.vaanigoel.vanaspati.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vaanigoel.vanaspati.HomeDashboard
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.FirebaseHelper

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Views
        val nameLayout = findViewById<TextInputLayout>(R.id.nameLayout)
        val emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)

        val btnSignup = findViewById<Button>(R.id.btnSignup)

        btnSignup.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Reset errors
            nameLayout.error = null
            emailLayout.error = null
            passwordLayout.error = null

            var isValid = true

            // Name validation
            if (name.isEmpty()) {
                nameLayout.error = "Name required"
                isValid = false
            }

            // Email validation
            if (email.isEmpty()) {
                emailLayout.error = "Email required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Enter valid email"
                isValid = false
            }

            // Password validation
            if (password.isEmpty()) {
                passwordLayout.error = "Password required"
                isValid = false
            } else if (password.length < 6) {
                passwordLayout.error = "Minimum 6 characters required"
                isValid = false
            }

            if (isValid) {

                Toast.makeText(
                    this,
                    "Creating Account...",
                    Toast.LENGTH_SHORT
                ).show()

                // Firebase Signup
                FirebaseHelper.auth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            // Save user to Firestore
                            val user = hashMapOf(
                                "name" to name,
                                "email" to email
                            )

                            FirebaseHelper.db
                                .collection("users")
                                .add(user)

                            Toast.makeText(
                                this,
                                "Signup Successful ✅",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Go to dashboard
                            val intent =
                                Intent(this, HomeDashboard::class.java)
                            startActivity(intent)

                            finish()

                        } else {

                            Toast.makeText(
                                this,
                                "Signup Failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
            }
        }
    }
}