package com.vaanigoel.vanaspati

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
// Using the binding you already imported!
import com.vaanigoel.vanaspati.databinding.ActivityHomeDashboardBinding

class HomeDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityHomeDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityHomeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup the Data (No need for "My Plants" as an item anymore)
        val plants = arrayOf("Rose")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, plants)

        // 2. Attach adapter to the AutoCompleteTextView
        binding.autoCompletePlants.setAdapter(adapter)

        // 3. Handle the selection (Visibility logic)
        binding.autoCompletePlants.setOnItemClickListener { parent, _, position, _ ->
            val selectedPlant = parent.getItemAtPosition(position).toString()

            when (selectedPlant) {
                "Rose" -> {
                    startActivity(Intent(this, RoseActivity::class.java))
                }
                // Add more plants here later
            }
        }

        // 4. Update the Plant Count Text
        val count = 1
        binding.tvPlantCount.text = if (count == 1) "You have 1 plant" else "You have $count plants"

        // 5. Add Plant Button logic
        binding.btnAddPlant.setOnClickListener {
            startActivity(Intent(this, AddPlantActivity::class.java))
        }
    }
}
