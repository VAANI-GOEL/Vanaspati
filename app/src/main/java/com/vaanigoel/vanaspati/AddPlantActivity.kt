package com.vaanigoel.vanaspati

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vaanigoel.vanaspati.databinding.ActivityAddPlantBinding

class AddPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlantBinding

    // Firebase — only Auth + Firestore, no Storage
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db   by lazy { FirebaseFirestore.getInstance() }

    // ── Plant type options ─────────────────────────────────────────────────────
    private val plantTypes = listOf(
        "Flowering Plant",
        "Succulent / Cactus",
        "Herb",
        "Vegetable",
        "Fruit Plant",
        "Creeper / Climber",
        "Tree",
        "Fern",
        "Aquatic Plant",
        "Other"
    )

    // ── onCreate ───────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ensureUserSignedIn()
        setupDropdown()
        setupClickListeners()

        // Pre-fill name if coming from a Quick Start chip on HomeDashboard
        intent.getStringExtra("PLANT_NAME")?.let {
            binding.etPlantName.setText(it)
        }
    }

    // ── Anonymous sign-in ──────────────────────────────────────────────────────
    // No email/password needed — each device gets a unique userId automatically.
    private fun ensureUserSignedIn() {
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnFailureListener { e ->
                    Log.e("AUTH", "Sign-in failed: ${e.message}")
                    Toast.makeText(this, "Auth failed — check internet", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // ── Dropdown setup ─────────────────────────────────────────────────────────
    private fun setupDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            plantTypes
        )
        binding.actvPlantType.setAdapter(adapter)
    }

    // ── Button listeners ───────────────────────────────────────────────────────
    private fun setupClickListeners() {
        binding.btnSavePlant.setOnClickListener {
            if (validateForm()) savePlant()
        }
    }

    // ── Validation ─────────────────────────────────────────────────────────────
    private fun validateForm(): Boolean {
        val name = binding.etPlantName.text.toString().trim()
        val type = binding.actvPlantType.text.toString().trim()

        if (name.isEmpty()) {
            binding.tilPlantName.error = "Please enter a plant name"
            return false
        }
        binding.tilPlantName.error = null

        if (type.isEmpty()) {
            binding.tilPlantType.error = "Please select a plant type"
            return false
        }
        binding.tilPlantType.error = null

        return true
    }

    // ── Save to Firestore ──────────────────────────────────────────────────────
    // Collection path: users/{userId}/plants/{auto-id}
    private fun savePlant() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Not signed in yet, try again", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val plant = hashMapOf(
            "name"      to binding.etPlantName.text.toString().trim(),
            "type"      to binding.actvPlantType.text.toString().trim(),
            "location"  to binding.etLocation.text.toString().trim(),
            "watering"  to binding.etWatering.text.toString().trim(),
            "notes"     to binding.etNotes.text.toString().trim(),
            "addedAt"   to Timestamp.now(),
            "userId"    to userId
        )

        db.collection("users")
            .document(userId)
            .collection("plants")
            .add(plant)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Plant saved! 🌿", Toast.LENGTH_SHORT).show()
                finish() // returns to HomeDashboard
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Log.e("FIRESTORE", "Save failed: ${e.message}")
                Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ── Loading state ──────────────────────────────────────────────────────────
    private fun setLoading(loading: Boolean) {
        binding.btnSavePlant.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }
}