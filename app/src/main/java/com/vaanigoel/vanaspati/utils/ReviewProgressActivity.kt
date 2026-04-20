package com.vaanigoel.vanaspati.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
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

    // Randomized priority list to balance server load across the CSE-3 batch
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
                Toast.makeText(this, "No photo captured", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCapturePhoto.setOnClickListener { openCameraFlow() }

        binding.btnCheckHealth.setOnClickListener {
            if (!isAnalyzing) {
                selectedBitmap?.let {
                    setLoadingState(true)
                    binding.tvResponse.text = ""
                    analyzePlant(it)
                } ?: run {
                    binding.tvResponse.text = "Please capture a photo first."
                    openCameraFlow()
                }
            }
        }
    }

    private fun openCameraFlow() {
        try {
            val photoFile = File.createTempFile("plant_capture", ".jpg", cacheDir)
            photoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
            takePhotoLauncher.launch(photoUri)
        } catch (e: Exception) {
            Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun analyzePlant(bitmap: Bitmap, modelIndex: Int = 0) {
        // Exit strategy if all models fail
        if (modelIndex >= modelPriorityList.size) {
            setLoadingState(false)
            binding.tvResponse.text = "Come back in some time...sorry for inconvenience❤\uFE0F\uD83D\uDE0C"
            return
        }

        val currentModel = modelPriorityList[modelIndex]
        binding.tvResponse.text = "Collecting leaves and flowers for you sweetheart...pls wait for a moment \uD83C\uDF42\uD83E\uDD40"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Encode once on the background thread
                val base64Image = encodeImage(bitmap)
                val dataUrl = "data:image/jpeg;base64,$base64Image"

                val request = OllamaRequest(
                    model = currentModel,
                    messages = listOf(
                        OllamaMessage(
                            role = "user",
                            content = listOf(
                                OllamaContent(
                                    type = "text",
                                    text = """
    Act as a friendly, expert Plant Advisor. Analyze this photo and reply in this exact way:
    
    1. Hey there! I see you've got a [Plant Name]... (Mention its Legume family status if it's Aparajita).
    2. Tell me the problem in one short, compassionate sentence.
    3. Give me your top 'Doctor's Order' to fix it immediately.
    4. Provide an HTML link for the best product like this:"https://www.amazon.com/s?k=[Product+Name]" Click here to find [Product Name] on Amazon</a>
    5. Provide a water guide in terms of mugs and glasses of water for the user.
    6. Share a quick 'Grandma's secret' home remedy.
    7.Homemade fertilizer.
    STRICT RULES: 
    - NEVER use ** asterisks or any bold symbols. 
    - Use HTML tags for the link.
    - Speak like a real person using 'I' and 'You'. 
    - Keep it under 60 words total.
"""
                                ),
                                OllamaContent(
                                    type = "image_url",
                                    imageUrl = OllamaImageUrl(dataUrl)
                                )
                            )
                        )
                    )
                )

                val response = RetrofitClient.instance.checkPlantHealth(
                    "Bearer ${BuildConfig.OPENROUTER_API_KEY}",
                    request
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        val rawContent = response.body()?.choices?.getOrNull(0)?.message?.content?.trim() ?: ""

                        setLoadingState(false)

                        // 1. Tell the TextView that the text contains HTML links
                        binding.tvResponse.text = android.text.Html.fromHtml(rawContent, android.text.Html.FROM_HTML_MODE_COMPACT)

                        // 2. THIS IS THE KEY: This makes the link actually open the browser
                        binding.tvResponse.movementMethod = android.text.method.LinkMovementMethod.getInstance()

                        // 3. Optional: Set a nice blue color for your links
                        binding.tvResponse.setLinkTextColor(android.graphics.Color.parseColor("#007BFF"))
                        binding.tvResponse.text = rawContent ?: "Response body was empty."
                    }
                    // Fallback logic for Rate Limits (429), Busy Servers (503), or Missing Endpoints (404)
                    else if (response.code() in listOf(404, 429, 502, 503)) {
                        Log.w("API_FALLBACK", "Model $currentModel failed with ${response.code()}. Retrying...")
                        analyzePlant(bitmap, modelIndex + 1)
                    }
                    else {
                        setLoadingState(false)
                        val errorTxt = response.errorBody()?.string() ?: "Unknown error"
                        binding.tvResponse.text = "Error ${response.code()}: $errorTxt"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Network timeouts or connection drops also trigger a fallback
                    Log.e("API_EXCEPTION", "Exception on $currentModel: ${e.message}")
                    analyzePlant(bitmap, modelIndex + 1)
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
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        selectedBitmap?.recycle()
        selectedBitmap = null
    }
}