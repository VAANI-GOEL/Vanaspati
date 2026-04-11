package com.vaanigoel.vanaspati.utils

data class PlantNetResponse(
    val results: List<Result>
)

data class Result(
    val species: Species,
    val score: Double
)

data class Species(
    val scientificNameWithoutAuthor: String,
    val commonNames: List<String>?
)