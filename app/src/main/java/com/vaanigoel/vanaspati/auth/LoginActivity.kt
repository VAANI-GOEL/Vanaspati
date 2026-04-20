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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailLayout      = findViewById<TextInputLayout>(R.id.emailLayout)
        val passwordLayout   = findViewById<TextInputLayout>(R.id.passwordLayout)
        val etEmail          = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword       = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin         = findViewById<Button>(R.id.btnLogin)
        val tvRegister       = findViewById<TextView>(R.id.tvRegister)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val prefs            = PrefsManager(this)

        tvRegister.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                emailLayout.error = "Enter your email first"
                return@setOnClickListener
            }
            FirebaseHelper.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            emailLayout.error    = null
            passwordLayout.error = null
            var isValid = true

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
                btnLogin.isEnabled = false
                btnLogin.text = "Signing in..."

                FirebaseHelper.auth
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        btnLogin.isEnabled = true
                        btnLogin.text = "Login"

                        if (task.isSuccessful) {
                            val uid = FirebaseHelper.auth.currentUser?.uid ?: ""
                            prefs.saveUser(uid = uid, email = email)
                            prefs.setLoggedIn(true)

                            startActivity(
                                Intent(this, HomeDashboard::class.java)
                                    .putExtra("username", email)
                            )
                            finish()
                        } else {
                    val errorCode = (task.exception as? com.google.firebase.auth.FirebaseAuthException)?.errorCode
                    Toast.makeText(
                        this,
                        "Code: $errorCode\n${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                    }
            }
        }
    }
}