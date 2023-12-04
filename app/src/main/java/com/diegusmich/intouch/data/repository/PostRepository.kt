package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.PostDTO
import com.diegusmich.intouch.data.model.ArchivedPostPreview
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object PostRepository : FirestoreCollection<PostDTO,PostDTO.Factory>(PostDTO.Factory::class.java) {

    private val server = Firebase.functions
    override val collectionRef = Firebase.firestore.collection("posts")

    suspend fun archivedPosts(userId: String) = withContext(Dispatchers.IO){
        withQuery{
            it.whereEqualTo("userId", userId).orderBy("createdAt", Query.Direction.ASCENDING)
        }.map {
            ArchivedPostPreview(
                id = it.id,
                thumbnail = it.album[0],
                createdAt = it.createdAt
            )
        }
    }

    suspend fun feed() = withContext(Dispatchers.IO){
        server.getHttpsCallable("posts-feed").call().await()
    }

    suspend fun viewPost(postId: String) = withContext(Dispatchers.IO){
        server.getHttpsCallable("posts-viewed").call(mapOf("postId" to postId)).await()
    }

    suspend fun deletePost() = withContext(Dispatchers.IO){
        //TODO()
    }
}