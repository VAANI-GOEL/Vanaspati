package com.vaanigoel.vanaspati.utils

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    private lateinit var binding: ActivityReviewProgressBinding
    private var photoUri: Uri? = null
    private var selectedBitmap: Bitmap? = null
    private var isAnalyzing = false

    private val modelPriorityList = mutableListOf(
        "nvidia/nemotron-nano-12b-v2-vl:free",
        "google/gemma-3-12b-it:free",
        "google/gemma-3-27b-it:free",
        "google/gemma-4-31b-it:free",
        "google/gemma-4-26b-a4b-it:free"

    )

    // ─── Permission launcher ───────────────────────────────────────────────────
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                launchCamera()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required to capture plant photos",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // ─── Camera launcher ──────────────────────────────────────────────────────
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
                    Toast.makeText(this, "Failed to load photo", Toast.LENGTH_SHORT).show()
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

    // ─── Camera flow with permission check ────────────────────────────────────
    private fun openCameraFlow() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(
                    this,
                    "Camera access is needed to photograph your plant",
                    Toast.LENGTH_LONG
                ).show()
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        try {
            val photoFile = File.createTempFile("plant_capture", ".jpg", cacheDir)
            photoUri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
            takePhotoLauncher.launch(photoUri)
        } catch (e: Exception) {
            Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ─── Plant analysis ───────────────────────────────────────────────────────
    private fun analyzePlant(bitmap: Bitmap, modelIndex: Int = 0) {
        if (modelIndex >= modelPriorityList.size) {
            setLoadingState(false)
            binding.tvResponse.text = "Come back in some time...sorry for inconvenience❤️😌"
            return
        }

        val currentModel = modelPriorityList[modelIndex]
        binding.tvResponse.text = "Collecting leaves and flowers for you sweetheart...pls wait for a moment 🍂🤀"

        lifecycleScope.launch(Dispatchers.IO) {
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
                                    text = """
Task: Analyze the plant photo provided and respond using the exact 7-point structure below.

STRICT STYLE & FORMATTING RULES:
Use bold text for important keywords and include emojis like 😊 🌿 💧 to keep the tone friendly.
Do not use markdown symbols except bold text.
Keep the entire response under 60 words.
Write in a natural human tone using I and You.
For the product link, use a working HTML anchor tag to Amazon.
Do not use ** symbols anywhere in the response.

REQUIRED RESPONSE STRUCTURE:

Greeting: Start with — Hey there! I see you have a [Plant Name].😊

Problem: Write one short, caring sentence describing the visible issue. 😟

Doctor's Order: Give one most important fix to do immediately. 🩺

Product Link: Use this exact format — <a href="https://www.amazon.com/s?k=[Product+Name]">Click here to find [Product Name] on Amazon</a> 🛒

Water Guide: Give watering advice using only mugs or glasses as units. 💧

Grandma's Secret: Share one simple home remedy. 👵

Fertilizer Tip: Suggest one homemade fertilizer tip. 🍌

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
                        val rawContent = response.body()
                            ?.choices?.getOrNull(0)
                            ?.message?.content
                            ?.trim() ?: ""

                        setLoadingState(false)

                        if (rawContent.isEmpty()) {
                            binding.tvResponse.text = "Response body was empty."
                        } else {
                            // ✅ Render HTML so <a href> tags become clickable links
                            binding.tvResponse.text = Html.fromHtml(
                                rawContent,
                                Html.FROM_HTML_MODE_COMPACT
                            )
                            // ✅ This makes links actually tappable
                            binding.tvResponse.movementMethod = LinkMovementMethod.getInstance()
                            // ✅ Nice blue color for links
                            binding.tvResponse.setLinkTextColor(Color.parseColor("#4CAF50"))
                        }

                    } else if (response.code() in listOf(404, 429, 502, 503)) {
                        Log.w("API_FALLBACK", "Model $currentModel failed with ${response.code()}. Retrying...")
                        analyzePlant(bitmap, modelIndex + 1)

                    } else {
                        setLoadingState(false)
                        val errorTxt = response.errorBody()?.string() ?: "Unknown error"
                        binding.tvResponse.text = "Error ${response.code()}: $errorTxt"
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API_EXCEPTION", "Exception on $currentModel: ${e.message}")
                    analyzePlant(bitmap, modelIndex + 1)
                }
            }
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
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

private fun ReviewProgressActivity.startCamera() {
    TODO("Not yet implemented")
}
