package com.vaanigoel.vanaspati
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseHelper {

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}
