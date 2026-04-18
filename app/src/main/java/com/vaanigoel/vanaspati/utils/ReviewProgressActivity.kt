package com.vaanigoel.vanaspati.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.vaanigoel.vanaspati.databinding.ActivityReviewProgressBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class ReviewProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewProgressBinding
    private var photoUri: Uri? = null
    private var selectedBitmap: Bitmap? = null

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) {
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(photoUri!!))
            val scaled = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true)
            binding.ivPlantPreview.setImageBitmap(scaled)
            selectedBitmap = scaled
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCapturePhoto.setOnClickListener {
            val photoFile = File.createTempFile("plant_capture", ".jpg", cacheDir)
            photoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
            takePhotoLauncher.launch(photoUri)
        }

        binding.btnCheckHealth.setOnClickListener {
            selectedBitmap?.let { analyzePlant(it) }
        }
    }

    private fun analyzePlant(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE
                // Better UI feedback for the presentation
                binding.tvResponse.text = "Initializing Neural Engine & Scanning Symptoms..."
            }

            try {
                val base64Image = encodeImage(bitmap)

                // 🌿 THE SMART PROMPT: Forces LLaVA to think like a pathologist
                val smartPrompt = """
                    [SYSTEM: ACT AS AN EXPERT BOTANICAL PATHOLOGIST]
    
    TASK: Analyze the provided image to identify the plant and diagnose its health status.
    
    THINK STEP-BY-STEP:
    1. Scan the image for the primary plant species.
    2. Examine the leaves for patterns: chlorosis (yellowing), necrosis (browning), spots, or curling.
    3. Look for environmental clues (soil moisture, pot size, light direction).
    
    OUTPUT FORMAT (Strictly follow this):
    1. **VISUAL SIGNS**:
       - [Bullet points describing specific leaf/stem observations]
    2. **DIAGNOSIS**: [Identify the single most likely issue: Overwatering, Underwatering, Pests, or Nutrient Deficiency]
    3. **SOLUTION**:
       - [Action 1: Immediate fix]
       - [Action 2: Long-term care adjustment]
       - [Action 3: Professional tip]
    
    [TONE: Clinical, expert, yet easy for a home gardener to understand. Be extremely concise.]
                """.trimIndent()

                val ollamaRequest = OllamaRequest(
                    model = "llava:latest",
                    prompt = smartPrompt, // Use the smart prompt here
                    images = listOf(base64Image)
                )

                val ollamaResponse = RetrofitClient.instance.checkPlantHealth(ollamaRequest)

                withContext(Dispatchers.Main) {
                    if (ollamaResponse.isSuccessful) {
                        val body = ollamaResponse.body()
                        val solution = body?.response ?: body?.message?.content ?: "Could not analyze health."

                        // Added a 🌱 emoji to make the output look more like a premium app
                        binding.tvResponse.text = "🌱 VANASPATI HEALTH REPORT:\n\n$solution"
                    } else {
                        // Helpful error for the demo
                        binding.tvResponse.text = "⚠️ Mac Connection Error: Ensure Ollama is running and OLLAMA_HOST is set."
                    }
                    binding.progressBar.visibility = View.GONE
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvResponse.text = "Error: Check if Mac & Phone are on same Wi-Fi."
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
    // --- Helper Functions Moved Here for Better Scoping ---

    private fun encodeImage(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    private fun bitmapToMultipart(bitmap: Bitmap): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val byteArray = stream.toByteArray()
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        return MultipartBody.Part.createFormData("images", "plant.jpg", requestBody)
    }
}