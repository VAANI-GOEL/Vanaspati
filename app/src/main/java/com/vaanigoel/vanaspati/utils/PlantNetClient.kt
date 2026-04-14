package com.vaanigoel.vanaspati.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PlantNetClient {

    private const val BASE_URL = "https://my-api.plantnet.org/"

    val instance: PlantNetApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlantNetApi::class.java)
    }
}