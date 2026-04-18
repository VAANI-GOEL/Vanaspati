package com.vaanigoel.vanaspati.utils

import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

fun bitmapToMultipart(bitmap: Bitmap): MultipartBody.Part {
    val stream = ByteArrayOutputStream()
    // 90% quality is perfect: high enough for PlantNet, small enough for network
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    val byteArray = stream.toByteArray()

    val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())

    return MultipartBody.Part.createFormData(
        "images", // PlantNet looks for this key
        "plant.jpg",
        requestBody
    )
}