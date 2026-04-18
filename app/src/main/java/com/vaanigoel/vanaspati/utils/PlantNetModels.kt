package com.vaanigoel.vanaspati.utils

import com.google.gson.annotations.SerializedName

data class PlantNetResponse(
    @SerializedName("results")
    val results: List<PlantResult>
)

data class PlantResult(
    @SerializedName("species")
    val species: PlantSpecies,
    @SerializedName("score")
    val score: Double
)

data class PlantSpecies(
    @SerializedName("scientificNameWithoutAuthor")
    val scientificNameWithoutAuthor: String,
    @SerializedName("commonNames")
    val commonNames: List<String>?
)