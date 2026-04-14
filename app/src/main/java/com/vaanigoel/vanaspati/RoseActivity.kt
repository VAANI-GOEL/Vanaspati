package com.vaanigoel.vanaspati

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vaanigoel.vanaspati.profile.ProfileActivity
import com.vaanigoel.vanaspati.utils.ReviewProgressActivity


// In RoseBoutiqueActivity.kt
class RoseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rose)
// Find the Review Progress Button
        val btnReviewProgress = findViewById<Button>(R.id.btnReviewProgress)

        // Set Click Listener to open the activity
        btnReviewProgress.setOnClickListener {
            val intent = Intent(this, ReviewProgressActivity::class.java)
            startActivity(intent)
        }

        // Find the Profile Circular Button
        val btnProfile = findViewById<View>(R.id.btnProfile)
        btnProfile.setOnClickListener {

             val intent = Intent(this, ProfileActivity::class.java)
             startActivity(intent)
        }
        // 1. Felco Pruners (Editor's Choice)
        findViewById<Button>(R.id.btnBuyFeatured).setOnClickListener {
            openAmazon("https://www.amazon.com/dp/B00023RYS6")
        }

        // 2. Potting Soil Mix
        // Assuming your 'item_product_small' has unique IDs or you set them programmatically
        findViewById<ImageButton>(R.id.btnBuySmall).setOnClickListener {
            openAmazon("https://www.amazon.com/s?k=organic+rose+potting+soil")
        }
    }

    private fun openAmazon(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}