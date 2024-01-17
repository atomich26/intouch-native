package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.wrapper.PostWrapper
import com.diegusmich.intouch.data.response.FeedPostsCallableResponse
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object PostRepository : FirestoreCollection<PostWrapper, PostWrapper.Factory>(PostWrapper.Factory::class.java) {

    override val collectionRef = Firebase.firestore.collection("posts")

    suspend fun archived(userId: String) = withContext(Dispatchers.IO) {
        withQuery {
            it.whereEqualTo("userId", userId).orderBy("createdAt", Query.Direction.ASCENDING)
        }.map {
          Post.ArchivePreview(it)
        }
    }

    suspend fun feed() : List<Post.FeedPreview> = withContext(Dispatchers.IO) {
        Firebase.functions.getHttpsCallable("posts-feed").call().await()?.let {
            FeedPostsCallableResponse(it).feedPosts.mapNotNull { feedPostWrapper ->
                UserRepository.getDoc(feedPostWrapper.userId)?.let{ userWrapper ->
                    Post.FeedPreview(feedPostWrapper, userWrapper)
                }
            }
        } ?: mutableListOf()
    }

    suspend fun getPost(id: String) = withContext(Dispatchers.IO){
        getDoc(id)?.let{ postWrapper ->
            UserRepository.getDoc(postWrapper.userId)?.let { userWrapper ->
                EventRepository.getDoc(postWrapper.eventId)?.let { eventWrapper ->
                    Post.Full(postWrapper, userWrapper, eventWrapper)
                }
            }
        }
    }

    suspend fun viewPost(postId: String) = withContext(Dispatchers.IO) {
        Firebase.functions.getHttpsCallable("posts-viewed").call(mapOf("postId" to postId)).await()
        true
    }

    suspend fun deletePost() = withContext(Dispatchers.IO) {
        //TODO()
    }
}