package com.vaanigoel.vanaspati.utils
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vaanigoel.vanaspati.databinding.ActivityReviewProgressBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ReviewProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewProgressBinding
    private var selectedBitmap: Bitmap? = null

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val scaled = Bitmap.createScaledBitmap(it, 1024, 1024, true)
            binding.ivPlantPreview.setImageBitmap(scaled)
            selectedBitmap = scaled
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCapturePhoto.setOnClickListener { takePhotoLauncher.launch(null) }
        binding.btnCheckHealth.setOnClickListener {
            selectedBitmap?.let { analyzePlant(it) }
        }
    }

    private fun analyzePlant(bitmap: Bitmap) {
        lifecycleScope.launch(Dispatchers.IO) {

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvResponse.text = "Analyzing plant..."
            }

            try {
                // 🌿 STEP 1: PlantNet API
                val imagePart = bitmapToMultipart(bitmap)

                val plantResponse = PlantNetClient.instance.identifyPlant(
                    images = listOf(imagePart),
                    organs = "leaf".toRequestBody("text/plain".toMediaType()),
                    apiKey = "YOUR_API_KEY"
                )

                val bestMatch = plantResponse.body()?.results?.firstOrNull()

                val plantName = bestMatch?.species?.commonNames?.firstOrNull()
                    ?: bestMatch?.species?.scientificNameWithoutAuthor
                    ?: "Unknown Plant"

                val confidence = bestMatch?.score?.times(100)?.toInt() ?: 0

                // 🤖 STEP 2: Ollama for tips
                val base64Image = encodeImage(bitmap)

                val ollamaRequest = OllamaRequest(
                    prompt = "The plant is $plantName. Give exactly 2 simple care tips.",
                    images = listOf(base64Image)
                )

                val ollamaResponse = RetrofitClient.instance.checkPlantHealth(ollamaRequest)

                withContext(Dispatchers.Main) {

                    val tips = if (ollamaResponse.isSuccessful && ollamaResponse.body() != null) {
                        val body = ollamaResponse.body()!!
                        body.response ?: body.message?.content ?: "No tips generated"
                    } else {
                        "Error getting tips"
                    }

                    binding.tvResponse.text = """
🌱 Plant: $plantName
$tips
                """.trimIndent()

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
}
private fun encodeImage(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
}