package com.vaanigoel.vanaspati.utils

//import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CareContentGenerator(private val prefs: PrefsManager) {

    // 1. Change modelName back to "gemini-pro"
    // 2. Remove any "generationConfig" or "requestOptions" that cause red errors
//    private val generativeModel = GenerativeModel(
//        modelName = "gemini-1.5-flash",
//        apiKey = "AIzaSyByxJLHPipRhnDOIMYf5O3yy8iEXjkEKg4"
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
}