package com.vaanigoel.vanaspati.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vaanigoel.vanaspati.FirebaseHelper
import com.vaanigoel.vanaspati.HomeDashboard
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.utils.PrefsManager

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameLayout     = findViewById<TextInputLayout>(R.id.nameLayout)
        val emailLayout    = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout = findViewById<TextInputLayout>(R.id.passwordLayout)
        val etName         = findViewById<TextInputEditText>(R.id.etName)
        val etEmail        = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword     = findViewById<TextInputEditText>(R.id.etPassword)
        val btnSignup      = findViewById<Button>(R.id.btnSignup)
        val tvLogin        = findViewById<TextView>(R.id.tvLogin)
        val prefs          = PrefsManager(this)

        tvLogin.setOnClickListener { finish() }

        btnSignup.setOnClickListener {
            val name     = etName.text.toString().trim()
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            nameLayout.error     = null
            emailLayout.error    = null
            passwordLayout.error = null
            var isValid = true

            if (name.isEmpty()) {
                nameLayout.error = "Name is required"
                isValid = false
            }
            if (email.isEmpty()) {
                emailLayout.error = "Email is required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Enter a valid email"
                isValid = false
            }
            if (password.isEmpty()) {
                passwordLayout.error = "Password is required"
                isValid = false
            } else if (password.length < 6) {
                passwordLayout.error = "Minimum 6 characters"
                isValid = false
            }

            if (isValid) {
                btnSignup.isEnabled = false
                btnSignup.text = "Creating account..."

                FirebaseHelper.auth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = FirebaseHelper.auth.currentUser?.uid
                                ?: run {
                                    btnSignup.isEnabled = true
                                    btnSignup.text = "Create Account"
                                    return@addOnCompleteListener
                                }

                            val user = hashMapOf("name" to name, "email" to email)

                            FirebaseHelper.db
                                .collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener {
                                    prefs.saveUser(uid = uid, email = email)
                                    prefs.setLoggedIn(true)
                                    Toast.makeText(this, "Welcome to Vanaspati!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, HomeDashboard::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    btnSignup.isEnabled = true
                                    btnSignup.text = "Create Account"
                                    Toast.makeText(this, "Error saving profile: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            btnSignup.isEnabled = true
                            btnSignup.text = "Create Account"
                            Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}