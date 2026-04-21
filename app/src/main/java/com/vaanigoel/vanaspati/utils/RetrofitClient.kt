package com.vaanigoel.vanaspati.utils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // --- CHANGED: Now pointing to the global OpenRouter Cloud API ---
    private const val BASE_URL = "https://openrouter.ai/api/v1/"


    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("HTTP-Referer", "https://vanaspati.app")  // your app name
                .addHeader("X-Title", "Vanaspati")                   // your app name
                .build()
            chain.proceed(request)
        }
        .build()

    val instance: OllamaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OllamaApi::class.java)
    }
}