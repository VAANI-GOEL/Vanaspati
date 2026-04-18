package com.vaanigoel.vanaspati.utils

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PlantNetApi {
    @Multipart
    @POST("v2/identify/all")
    suspend fun identifyPlant(
        @Query("api-key") apiKey: String,
        @Part images: List<MultipartBody.Part>, // Must match 'images' in Activity
        @Part("organs") organs: RequestBody      // Must match 'organs' in Activity
    ): Response<PlantNetResponse>
}