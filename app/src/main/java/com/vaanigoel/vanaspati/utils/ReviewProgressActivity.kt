package com.vaanigoel.vanaspati.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.util.Base64
import android.util.Log
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

    // Shuffled once per launch so all users don't hammer model[0] simultaneously
    private val modelPriorityList = mutableListOf(
        "google/gemma-4-31b-it:free",
        "google/gemma-4-26b-a4b-it:free",
        "google/gemma-3-27b-it:free",
        "google/gemma-3n-e4b-it:free",
        "google/gemma-3-12b-it:free"
    ).also { it.shuffle() }

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri != null) {
                val raw = BitmapFactory.decodeStream(contentResolver.openInputStream(photoUri!!))
                val scaled = scaleBitmap(raw)
                binding.ivPlantPreview.setImageBitmap(scaled)
                // Recycle old bitmap before replacing
                selectedBitmap?.recycle()
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
            if (!isAnalyzing) {
                selectedBitmap?.let {
                    setLoadingState(true)
                    binding.tvResponse.text = ""
                    analyzePlant(it)
                } ?: run {
                    binding.tvResponse.text = "Please capture a photo first."
                }
            }
        }
    }

    private fun analyzePlant(bitmap: Bitmap, modelIndex: Int = 0) {
        // All models exhausted
        if (modelIndex >= modelPriorityList.size) {
            setLoadingState(false)
            binding.tvResponse.text =
                "All AI engines are currently busy. Please try again in a moment."
            return
        }

        val currentModel = modelPriorityList[modelIndex]
        val totalModels = modelPriorityList.size
        binding.tvResponse.text = "Trying engine ${modelIndex + 1} of $totalModels…"

        val base64Image = encodeImage(bitmap)
        val dataUrl = "data:image/jpeg;base64,$base64Image"

        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("API_KEY_CHECK", "Key = '${BuildConfig.OPENROUTER_API_KEY}'")
            Log.d("API_DEBUG", "Calling model: $currentModel")

            try {
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
                                    text = "Identify this plant and check its health."
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

                Log.d("API_DEBUG", "Response code: ${response.code()}")
                Log.d("API_DEBUG", "Response body: ${response.errorBody()?.string()}")

                withContext(Dispatchers.Main) {
                    when {
                        response.isSuccessful -> {
                            val content = response.body()
                                ?.choices
                                ?.getOrNull(0)
                                ?.message
                                ?.content
                                ?.trim()
                            setLoadingState(false)
                            binding.tvResponse.text = if (!content.isNullOrBlank()) content
                            else "No response received."
                        }
                        // Rate limit, overload, OR no endpoints — try next model
                        response.code() == 429 || response.code() == 503 || response.code() == 404 -> {
                            Log.d("API_DEBUG", "Code ${response.code()} on $currentModel — trying next")
                            analyzePlant(bitmap, modelIndex + 1)
                        }
                        else -> {
                            setLoadingState(false)
                            val errorBody = response.errorBody()?.string()
                            Log.e("API_ERROR", "Code: ${response.code()}, Body: $errorBody")
                            // Show full error on screen so you can read it
                            binding.tvResponse.text = "Code ${response.code()}\n\n$errorBody"
                        }
                    }
                }

            } catch (e: Exception) {
                // This will now show the REAL error
                Log.e("API_EXCEPTION", "Type: ${e.javaClass.simpleName}")
                Log.e("API_EXCEPTION", "Message: ${e.message}")
                Log.e("API_EXCEPTION", "Cause: ${e.cause}")
                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    setLoadingState(false)
                    // Show exception directly on screen
                    binding.tvResponse.text = "${e.javaClass.simpleName}:\n${e.message}"
                }
            }
        }
    }

    // Scale down preserving aspect ratio — avoids distortion and OOM
    private fun scaleBitmap(bitmap: Bitmap, maxDim: Int = 1024): Bitmap {
        val ratio = minOf(
            maxDim.toFloat() / bitmap.width,
            maxDim.toFloat() / bitmap.height
        )
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