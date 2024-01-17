package com.diegusmich.intouch.providers

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.security.MessageDigest
import java.util.Date

enum class CloudImageProvider(path: String) {

    CATEGORIES("categories"),
    USERS("users"),
    POSTS("posts"),
    EVENTS("events");

    private val storageRef = Firebase.storage.reference.child(path)

    fun imageRef(namePath: String): StorageReference? {
        return if (namePath.isNotBlank())
            storageRef.child(namePath)
        else null
    }

    suspend fun uploadImage(imageUri: Uri): StorageReference {
        val hashedBytes = MessageDigest.getInstance("MD5").digest(AuthProvider.authUser()?.uid?.toByteArray()!!)
        val hexString = hashedBytes.joinToString("") { "%02x".format(it) }

        val fileRef = storageRef.child("${Date().time}_$hexString.jpg")
        fileRef.putFile(imageUri).await()

        return fileRef
    }
}