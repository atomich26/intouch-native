package com.diegusmich.intouch.providers

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class CloudImageProvider(path : String) {

    private val storageRef = Firebase.storage.reference.child(path)

    fun imageRef(namePath : String) : StorageReference? {
        return if(namePath.isNotBlank())
            storageRef.child(namePath)
        else null
    }

    suspend fun uploadImage(uri : Uri){
        storageRef.putFile(uri).await()
    }

    companion object{
        val CATEGORIES = CloudImageProvider("categories")
        val USERS = CloudImageProvider("users")
        val POSTS = CloudImageProvider("posts")
        val EVENTS = CloudImageProvider("events")
    }
}