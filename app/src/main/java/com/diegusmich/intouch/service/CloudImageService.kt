package com.diegusmich.intouch.service

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class CloudImageService(path : String) {

    private val storageRef = Firebase.storage.reference.child(path)

    fun imageRef(namePath : String) : StorageReference {
        return storageRef.child(namePath)
    }

    suspend fun uploadImage(uri : Uri){
        storageRef.putFile(uri).await()
    }

    companion object{
        val CATEGORIES = CloudImageService("categories")
        val USERS = CloudImageService("users")
        val POSTS = CloudImageService("posts")
        val EVENTS = CloudImageService("events")
    }
}