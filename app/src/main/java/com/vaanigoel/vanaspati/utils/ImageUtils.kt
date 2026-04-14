package com.vaanigoel.vanaspati.utils

import android.graphics.Bitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

fun bitmapToMultipart(bitmap: Bitmap): MultipartBody.Part {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)

    val requestBody = RequestBody.create(
        "image/jpeg".toMediaTypeOrNull(),
        stream.toByteArray()
    )

    return MultipartBody.Part.createFormData(
        "images",
        "plant.jpg",
        requestBody
    )
}