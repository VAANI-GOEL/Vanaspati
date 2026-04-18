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
                binding.tvResponse.text = "Cloud AI is diagnosing symptoms..."
            }

            try {
                val base64Image = encodeImage(bitmap)
                // Cloud APIs need the "data:image" prefix
                val dataUrl = "data:image/jpeg;base64,$base64Image"

                // Constructing the message with your Smart Prompt
                val message = OllamaMessage(
                    content = listOf(
                        OllamaContent(type = "text", text = """
                        Identify this plant and perform a clinical health check.
                        1. Plant Name: [Common Name]
                        2. VISUAL SIGNS: Describe color patterns and edges.
                        3. DIAGNOSIS: Overwatering, Underwatering, Pests, or Deficiency.
                        4. SOLUTION: Give 3 professional steps.
                        Format with bold headers. Be concise.
                    """.trimIndent()),
                        OllamaContent(type = "image_url", imageUrl = OllamaImageUrl(dataUrl))
                    )
                )

                val request = OllamaRequest(messages = listOf(message))

                // Replace with your actual OpenRouter Key
                val response = RetrofitClient.instance.checkPlantHealth(
                    "Bearer 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",
                    request
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val solution = response.body()?.choices?.firstOrNull()?.message?.content
                        binding.tvResponse.text = solution ?: "Analysis failed."
                    } else {
                        binding.tvResponse.text = "Error: Quota reached or API issue."
                    }
                    binding.progressBar.visibility = View.GONE
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvResponse.text = "Error: ${e.localizedMessage}"
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