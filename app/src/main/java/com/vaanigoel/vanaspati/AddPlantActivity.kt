package com.vaanigoel.vanaspati

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vaanigoel.vanaspati.databinding.ActivityAddPlantBinding
import com.vaanigoel.vanaspati.utils.ReviewProgressActivity

class AddPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPlantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Hide the Take Photo and Gallery buttons
        binding.btnCamera.visibility = View.GONE
        binding.btnGallery.visibility = View.GONE

        // 2. Set the "Identify Plant from AI" button to open the Analysis screen
        binding.btnIdentify.setOnClickListener {
            val intent = Intent(this@AddPlantActivity, ReviewProgressActivity::class.java)
            startActivity(intent)
        }

        // Note: You can also remove the buttons entirely from activity_add_plant.xml
        // to save space on the screen.
    }
}