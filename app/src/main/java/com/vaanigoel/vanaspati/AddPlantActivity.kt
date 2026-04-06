package com.vaanigoel.vanaspati

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.vaanigoel.vanaspati.databinding.ActivityAddPlantBinding

class AddPlantActivity : AppCompatActivity() {

    // This connects our Kotlin code to activity_add_plant.xml
    private lateinit var binding: ActivityAddPlantBinding

    // Launcher for Gallery
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { binding.imageView.setImageURI(it) }
    }

    // Launcher for Camera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let { binding.imageView.setImageBitmap(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Button Click Listeners
        binding.btnCamera.setOnClickListener {
            cameraLauncher.launch(null)
        }

        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.btnIdentify.setOnClickListener {
            // This is where we will call the AI later!
        }
    }
}