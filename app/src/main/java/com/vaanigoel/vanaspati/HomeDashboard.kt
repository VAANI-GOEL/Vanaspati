package com.vaanigoel.vanaspati

import com.vaanigoel.vanaspati.databinding.ActivityHomeDashboardBinding
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView



class HomeDashboard : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_dashboard)

        val tvPlantCount = findViewById<TextView>(R.id.tvPlantCount)
        val btnAddPlant = findViewById<Button>(R.id.btnAddPlant)

        //Temporary data
        tvPlantCount.text = "You have 0 plants"

        btnAddPlant.setOnClickListener {
            startActivity(Intent(this, AddPlantActivity::class.java))
        }

        }

    }
