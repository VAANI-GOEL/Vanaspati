package com.vaanigoel.vanaspati.utils

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// --- Data Models ---
data class OllamaRequest(
    val model: String = "llava",
    val prompt: String,
    val stream: Boolean = false,
    val images: List<String>? = null,
    val options: OllamaOptions = OllamaOptions()
)

data class OllamaOptions(
    @SerializedName("num_predict")
    val numPredict: Int = 200,
    val temperature: Float = 0.4f
)

data class OllamaResponse(
    val response: String?,       // Terminal usually shows this
    val message: OllamaMessage?, // Chat mode uses this
    val done: Boolean
)

data class OllamaMessage(
    val content: String
)

// --- The Interface ---
interface OllamaApi {
    @POST("api/generate")
    suspend fun checkPlantHealth(@Body request: OllamaRequest): Response<OllamaResponse>
}