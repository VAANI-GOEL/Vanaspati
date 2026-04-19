package com.vaanigoel.vanaspati.utils

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.vaanigoel.vanaspati.BuildConfig
import com.vaanigoel.vanaspati.databinding.ActivityReviewProgressBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

class ReviewProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewProgressBinding
    private var photoUri: Uri? = null
    private var selectedBitmap: Bitmap? = null
    private var isAnalyzing = false

    private val modelPriorityList = mutableListOf(
        "google/gemma-4-31b-it:free",
        "google/gemma-4-26b-a4b-it:free",
        "google/gemma-3-27b-it:free",
        "nvidia/nemotron-nano-12b-v2-vl:free",
        "google/gemma-3-12b-it:free"
    ).also { it.shuffle() }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri != null) {
                try {
                    val raw = BitmapFactory.decodeStream(contentResolver.openInputStream(photoUri!!))
                    val scaled = scaleBitmap(raw)
                    binding.ivPlantPreview.setImageBitmap(scaled)
                    selectedBitmap?.recycle()
                    selectedBitmap = scaled
                } catch (e: Exception) {
                    Log.e("CAMERA_ERROR", "Failed to decode photo: ${e.message}")
                }
            } else {
                // If they cancel the camera on the first auto-launch,
                // they just stay on the grid screen.
                Toast.makeText(this, "No photo captured", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- STEP 2 MODIFICATION: AUTO-LAUNCH ---
        // This triggers the camera as soon as the Activity starts
        //openCameraFlow()

        binding.btnCapturePhoto.setOnClickListener {
            openCameraFlow()
        }

        binding.btnCheckHealth.setOnClickListener {
            if (!isAnalyzing) {
                selectedBitmap?.let {
                    setLoadingState(true)
                    binding.tvResponse.text = ""
                    analyzePlant(it)
                } ?: run {
                    binding.tvResponse.text = "Please capture a photo first."
                    // Optional: If no photo, open camera for them
                    openCameraFlow()
                }
            }
        }
    }

    // Created this function so we don't repeat code for onCreate and btnClick
    private fun openCameraFlow() {
        try {
            val photoFile = File.createTempFile("plant_capture", ".jpg", cacheDir)
            photoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
            takePhotoLauncher.launch(photoUri)
        } catch (e: Exception) {
            Log.e("CAMERA_INIT_FAIL", e.message ?: "Unknown error")
            Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun analyzePlant(bitmap: Bitmap, modelIndex: Int = 0) {
        if (modelIndex >= modelPriorityList.size) {
            setLoadingState(false)
            binding.tvResponse.text = "All AI engines are currently busy. Please try again."
            return
        }

        val currentModel = modelPriorityList[modelIndex]
        val totalModels = modelPriorityList.size
        binding.tvResponse.text = "Trying engine ${modelIndex + 1} of $totalModels…"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val base64Image = encodeImage(bitmap)
                val dataUrl = "data:image/jpeg;base64,$base64Image"

                // ... (Rest of your Retrofit / API logic remains the same) ...
                // Ensure your Retrofit call and response handling follows here

                // Example of where you'd call the next model on failure:
                // if (response.code() == 429) analyzePlant(bitmap, modelIndex + 1)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setLoadingState(false)
                    binding.tvResponse.text = "Error: ${e.message}"
                }
            }
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDim: Int = 1024): Bitmap {
        val ratio = minOf(maxDim.toFloat() / bitmap.width, maxDim.toFloat() / bitmap.height)
        if (ratio >= 1f) return bitmap
        val w = (bitmap.width * ratio).toInt()
        val h = (bitmap.height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, w, h, true).also {
            if (it !== bitmap) bitmap.recycle()
        }
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    private fun setLoadingState(loading: Boolean) {
        isAnalyzing = loading
        binding.btnCheckHealth.isEnabled = !loading
        binding.btnCapturePhoto.isEnabled = !loading
        // Make sure you have a progressBar in your XML or remove this line
        // binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        selectedBitmap?.recycle()
        selectedBitmap = null
    }
}