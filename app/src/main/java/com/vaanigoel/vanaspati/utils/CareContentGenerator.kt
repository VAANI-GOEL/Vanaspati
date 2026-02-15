package com.vaanigoel.vanaspati.utils

import com.google.genai.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//class CareContentGenerator(private val prefs: PrefsManager) {

    // 1. Change modelName back to "gemini-pro"
    // 2. Remove any "generationConfig" or "requestOptions" that cause red errors
//    private val generativeModel = GenerativeModel(
//        modelName = "gemini-1.5-flash",
//
//    )

//    suspend fun generateAiTip(plantName: String): String = withContext(Dispatchers.IO) {
//        val level = prefs.getHelpLevel()
//        val lang = prefs.getLanguage()

//        val prompt = """
//            You are an expert Indian gardening assistant named Vanaspati.
//            Give a 1-sentence care tip for a $plantName.
//            Language: $lang.
//            User Experience Level: $level.
//            Crucial: Use Indian household measurements like 'tea cups', 'spoons', or 'fingers'.
//        """.trimIndent()

//        try {
            // 3. Keep the simple call.
            // If this fails, the 'catch' block will handle the UI.
//            val response = generativeModel.generateContent(prompt)
//            response.text ?: "Check your $plantName's soil moisture today!"
//        } catch (e: Exception) {
//            android.util.Log.e("GeminiError", "Failed: ${e.message}")
            // 4. Return a "Safe" fallback tip so the app doesn't look broken
//            "Give your $plantName some water and ensure it gets enough sunlight."
//        }
//    }
//}

class GenAiHelper(apiKey: String) {
    // Initialize the new unified client
//    private val client = Client.builder()
//
//        .build()

//    suspend fun getPlantCareTip(plantName: String): String? = withContext(Dispatchers.IO) {
//        try {
//            // Using the latest 2026 stable model: gemini-3-flash
//            // Using the latest 2026 Unified SDK
//            val response = client.models.generateContent("gemini-3-flash", "Tell me how to grow a sunflower.")
//
//// Call the function 'text()' to get the String
//            val resultText: String = response.text()
//
//// Now you can use the String normally
//            println("AI says: $resultText")
//            resultText
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
}