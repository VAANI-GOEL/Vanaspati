package com.vaanigoel.vanaspati.auth

import android.os.Bundle
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vaanigoel.vanaspati.R
import com.vaanigoel.vanaspati.profile.ProfileActivity
import android.widget.Button
import com.vaanigoel.vanaspati.utils.PrefsManager
import android.widget.EditText
import android.widget.Toast


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.etUsername)
        val password = findViewById<EditText>(R.id.etPassword)
        val loginBtn = findViewById<Button>(R.id.btnLogin)
        val prefs = PrefsManager(this)
        loginBtn.setOnClickListener {
            if (username.text.toString().isNotEmpty() &&
                password.text.toString().isNotEmpty()) {
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

                Toast.makeText(this, "Login Successful 🌿", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}