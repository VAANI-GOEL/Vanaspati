package com.vaanigoel.vanaspati.utils

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PlantNetApi {

    @Multipart
    @POST("v2/identify/all")
    suspend fun identifyPlant(
        @Part images: List<MultipartBody.Part>,
        @Part("organs") organs: RequestBody,
        @Query("api-key") apiKey: String
    ): Response<PlantNetResponse>
}