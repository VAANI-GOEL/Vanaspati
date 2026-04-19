package com.vaanigoel.vanaspati

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)

        // Click image → open gallery
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // After user selects image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data

            imageView.setImageURI(imageUri)

            imageUri?.let {
                convertToBase64(it)
            }
        }
    }

    // Convert image → Base64
    private fun convertToBase64(uri: Uri) {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)

        println("Base64 ready")

        // 🔥 STEP 2: Call PlantNet API
        sendToPlantNet(base64)
    }

    // 🔥 STEP 2 FUNCTION (Pl@ntNet API)
    private fun sendToPlantNet(base64: String) {

        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("images", base64)
            .addFormDataPart("organs", "leaf")
            .build()

        val request = Request.Builder()
            .url("https://my-api.plantnet.org/v2/identify/all?api-key=\${BuildConfig.API_KEY}")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                println("PlantNet Response: $result")

                val plantName = extractPlantName(result!!)
                println("Detected Plant: $plantName")

                // 👉 Next step will be Ollama
            }
        })
    }

    // 🔥 Extract plant name
    private fun extractPlantName(response: String): String {
        val json = JSONObject(response)

        val results = json.getJSONArray("results")

        if (results.length() > 0) {
            val species = results.getJSONObject(0).getJSONObject("species")
            return species.getString("scientificNameWithoutAuthor")
        }

        return "Unknown Plant"
    }
}