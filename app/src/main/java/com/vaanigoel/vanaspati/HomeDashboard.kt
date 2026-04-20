//package com.vaanigoel.vanaspati
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import androidx.appcompat.app.AppCompatActivity
//// Using the binding you already imported!
//import com.vaanigoel.vanaspati.databinding.ActivityHomeDashboardBinding
//
//class HomeDashboard : AppCompatActivity() {
//
//    private lateinit var binding: ActivityHomeDashboardBinding
//
//    override fun onCreate(savedInstanceState: Bundle?)
//    {
//        super.onCreate(savedInstanceState)
//
//        // Initialize View Binding
//        binding = ActivityHomeDashboardBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // 1. Setup the Data (No need for "My Plants" as an item anymore)
////        val plants = arrayOf("Rose")
////        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, plants)
//
//        // 2. Attach adapter to the AutoCompleteTextView
////        binding.autoCompletePlants.setAdapter(adapter)
////
////        // 3. Handle the selection (Visibility logic)
////        binding.autoCompletePlants.setOnItemClickListener { parent, _, position, _ ->
////            val selectedPlant = parent.getItemAtPosition(position).toString()
//
////            when (selectedPlant) {
////                "Rose" -> {
////                    startActivity(Intent(this, RoseActivity::class.java))
////    }    // Add more plants here later
////            }
////        }
//
////        // 4. Update the Plant Count Text
////        val count = 1
////        binding.tvPlantCount.text = if (count == 1) "You have 1 plant" else "You have $count plants"
////
//        //5. Add Plant Button logic
//        binding.btnAddPlant.setOnClickListener {
//            startActivity(Intent(this, AddPlantActivity::class.java))
//        }
//   }
//}
package com.vaanigoel.vanaspati

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.vaanigoel.vanaspati.databinding.ActivityHomeDashboardBinding

class HomeDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityHomeDashboardBinding

    // ── Care tip carousel ──────────────────────────────────────────────────────
    private val careTips = listOf(
        "💧  Most plants prefer to dry out slightly between watering.",
        "☀️  Rotate your pots every week for even sunlight exposure.",
        "🌿  Wipe dusty leaves with a damp cloth to help them breathe.",
        "🪴  Repot when roots start peeking out of drainage holes.",
        "🌡️  Keep plants away from AC vents — they hate cold drafts."
    )
    private var tipIndex = 0
    private val tipHandler = Handler(Looper.getMainLooper())
    private val tipRunnable = object : Runnable {
        override fun run() {
            tipIndex = (tipIndex + 1) % careTips.size
            binding.tvCareTip.text = careTips[tipIndex]
            tipHandler.postDelayed(this, 4000)
        }
    }

    // ── Quick-action data ──────────────────────────────────────────────────────
    // Each pair is (emoji, label) — purely visual, no backend needed
    private val quickActions = listOf(
        Pair("🌵", "Cactus"),
        Pair("🌿", "Pothos"),
        Pair("🌹", "Rose"),
        Pair("🌻", "Sunflower"),
        Pair("🍃", "Fern")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGreeting()
        setupCareTips()
        setupQuickActions()
        setupButtons()
    }

    // ── Greeting changes by time of day ───────────────────────────────────────
    private fun setupGreeting() {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 5..11  -> "Good morning 🌅"
            in 12..16 -> "Good afternoon 🌞"
            in 17..20 -> "Good evening 🌇"
            else      -> "Good night 🌙"
        }
        // Set in your XML TextView with id: tvGreeting
        binding.tvGreeting.text = greeting

        // Subtitle — no plant count since no backend
        // Set in your XML TextView with id: tvSubtitle
        binding.tvSubtitle.text = "Keep your garden happy 🌿"
    }

    // ── Rotating care tips ─────────────────────────────────────────────────────
    private fun setupCareTips() {
        // Set in your XML TextView with id: tvCareTip
        binding.tvCareTip.text = careTips[0]
        tipHandler.postDelayed(tipRunnable, 4000)
    }

    // ── Quick-action chips (popular plant types) ───────────────────────────────
    // These open AddPlantActivity with the plant name pre-filled as an Intent extra.
    // In AddPlantActivity, read it with: intent.getStringExtra("PLANT_NAME")
    private fun setupQuickActions() {
        // Map chip IDs to quickActions list index
        // Add these 5 Chips to a HorizontalScrollView in your XML:
        //   id: chip0, chip1, chip2, chip3, chip4
        val chipIds = listOf(
            binding.chip0,
            binding.chip1,
            binding.chip2,
            binding.chip3,
            binding.chip4
        )
        chipIds.forEachIndexed { i, chip ->
            chip.text = "${quickActions[i].first} ${quickActions[i].second}"
            chip.setOnClickListener {
                val intent = Intent(this, AddPlantActivity::class.java)
                intent.putExtra("PLANT_NAME", quickActions[i].second)
                startActivity(intent)
            }
        }
    }

    // ── Main buttons ───────────────────────────────────────────────────────────
    private fun setupButtons() {

        // Primary CTA — goes to AddPlantActivity (same as before)
        // XML id: btnAddPlant
        binding.btnAddPlant.setOnClickListener {
            startActivity(Intent(this, AddPlantActivity::class.java))
        }

        // Secondary CTA — goes straight to the AI identify screen
        // XML id: btnIdentifyNow
        binding.btnIdentifyNow.setOnClickListener {
            startActivity(Intent(this, com.vaanigoel.vanaspati.utils.ReviewProgressActivity::class.java))
        }
    }

    // ── Lifecycle: stop tip rotation when app is in background ────────────────
    override fun onPause() {
        super.onPause()
        tipHandler.removeCallbacks(tipRunnable)
    }

    override fun onResume() {
        super.onResume()
        tipHandler.postDelayed(tipRunnable, 4000)
    }
}
