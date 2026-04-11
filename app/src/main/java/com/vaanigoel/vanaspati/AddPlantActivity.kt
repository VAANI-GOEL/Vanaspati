package com.vaanigoel.vanaspati

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.vaanigoel.vanaspati.databinding.ActivityAddPlantBinding

class AddPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlantBinding

    // Launcher for Gallery
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { binding.imageView.setImageURI(it) }
    }

    // Launcher for Camera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            binding.imageView.setImageBitmap(it)
        } ?: Toast.makeText(this, "No photo captured", Toast.LENGTH_SHORT).show()
    }

    // 1. New Launcher for Permission Request
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launchCameraSafe()
        } else {
            Toast.makeText(this, "Camera permission is denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Take Photo Button
        binding.btnCamera.setOnClickListener {
            checkAndLaunchCamera()
        }

        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnIdentify.setOnClickListener {
            // AI call goes here
        }
    }

    private fun checkAndLaunchCamera() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                launchCameraSafe()
            }
            else -> {
                // Ask for permission
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCameraSafe() {
        try {
            cameraLauncher.launch(null)
        } catch (e: Exception) {
            Log.e("VANASPATI_ERROR", "Camera fail: ${e.message}")
            Toast.makeText(this, "Camera app not found or error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}