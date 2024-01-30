package com.diegusmich.intouch.providers

import android.util.Log
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

    fun newFileRef(): StorageReference {
        val hashedBytes =
            MessageDigest.getInstance("MD5").digest(AuthProvider.authUser()?.uid?.toByteArray()!!)
        val hexString = hashedBytes.joinToString("") { "%02x".format(it) }

        return storageRef.child("${Date().time}_$hexString.jpg")
    }

    suspend fun uploadImage(image: File, ref: StorageReference): StorageReference {
        ref.putBytes(image.readBytes()).await()
        return ref
    }

    fun deleteImage(imagePath: String) {
        try {
            storageRef.child(imagePath).delete()
        } catch (e: Exception) {
            Log.e("CLOUD_IMAGE_PROVIDER", e.message.toString())
        }
    }
}