package com.vaanigoel.vanaspati.utils

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// --- Updated Data Models (OpenRouter Compatible) ---

data class OllamaRequest(

    val model: String = "google/gemma-3-27b-it:free", // Free Cloud Vision Model
    val messages: List<OllamaMessage>
)

data class OllamaMessage(
    val role: String = "user",
    val content: List<OllamaContent>
)

data class OllamaContent(
    val type: String, // "text" or "image_url"
    val text: String? = null,
    @SerializedName("image_url")
    val imageUrl: OllamaImageUrl? = null
)

data class OllamaImageUrl(
    val url: String // Format: "data:image/jpeg;base64,..."
)

data class OllamaResponse(
    val choices: List<OllamaChoice>?
)

data class OllamaChoice(
    val message: OllamaResponseMessage
)

data class OllamaResponseMessage(
    val content: String
)

// --- Updated Interface ---
interface OllamaApi {
    @POST("chat/completions")
    suspend fun checkPlantHealth(
        @Header("Authorization") apiKey: String, // Pass "Bearer YOUR_KEY"
        @Body request: OllamaRequest
    ): Response<OllamaResponse>
}