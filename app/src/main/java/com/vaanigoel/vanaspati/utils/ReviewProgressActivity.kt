package com.vaanigoel.vanaspati.utils

import android.graphics.Color
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.vaanigoel.vanaspati.R

class ReviewProgressActivity : AppCompatActivity() {
    // 1. Define these at the very top of your class (outside onCreate)
    private var waterValue = 40
    private var manureValue = 30
    private var sunValue = 0 // Start at 0% sunlight

    private var isSunActive = false
    private var isWaterActive = false
    private var isManureActive = false

    // 2. Inside onCreate, set up your listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_progress)

        val tvPercentage = findViewById<TextView>(R.id.tvPercentage)
        val pbWater = findViewById<ProgressBar>(R.id.pbWater)
        val pbManure = findViewById<ProgressBar>(R.id.pbManure)

        val btnSun = findViewById<MaterialCardView>(R.id.btnLogSun)
        val btnWater = findViewById<MaterialCardView>(R.id.btnLogWater)
        val btnManure = findViewById<MaterialCardView>(R.id.btnLogManure)

        // SUN BUTTON CLICK
        btnSun.setOnClickListener {
            isSunActive = !isSunActive
            sunValue = if (isSunActive) 100 else 0

            // Update Visuals
            btnSun.setCardBackgroundColor(if (isSunActive) Color.parseColor("#FFC107") else Color.parseColor("#0D3B0D"))

            // RE-CALCULATE EVERYTHING
            updateVitality(tvPercentage, pbWater, pbManure)
        }

        // WATER BUTTON CLICK
        btnWater.setOnClickListener {
            isWaterActive = !isWaterActive
            waterValue = if (isWaterActive) 100 else 40
            btnWater.setCardBackgroundColor(if (isWaterActive) Color.parseColor("#4CAF50") else Color.parseColor("#0D3B0D"))

            updateVitality(tvPercentage, pbWater, pbManure)
        }

        // MANURE BUTTON CLICK
        btnManure.setOnClickListener {
            isManureActive = !isManureActive
            manureValue = if (isManureActive) 100 else 30
            btnManure.setCardBackgroundColor(if (isManureActive) Color.parseColor("#8D6E63") else Color.parseColor("#0D3B0D"))

            updateVitality(tvPercentage, pbWater, pbManure)
        }
    }

    // 3. THE "BRAIN" OF THE PAGE
    private fun updateVitality(tv: TextView, pbW: ProgressBar, pbM: ProgressBar) {
        // Math: Average of all three care steps
        val total = (waterValue + manureValue + sunValue) / 3

        // Animate bars
        pbW.setProgress(waterValue, true)
        pbM.setProgress(manureValue, true)

        // Update Text
        tv.text = "$total%"

        // Color Feedback
        if (total > 70) tv.setTextColor(Color.parseColor("#81C784")) // Green
        else tv.setTextColor(Color.parseColor("#E57373")) // Red/Pink
    }
}