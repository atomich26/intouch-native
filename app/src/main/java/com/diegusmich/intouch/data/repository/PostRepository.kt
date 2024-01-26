package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.domain.Comment
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.wrapper.PostWrapper
import com.diegusmich.intouch.data.response.FeedPostsCallableResponse
import com.diegusmich.intouch.data.wrapper.CommentWrapper
import com.diegusmich.intouch.providers.AuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

object PostRepository : FirestoreCollection<PostWrapper, PostWrapper.Factory>(PostWrapper.Factory::class.java) {

    override val collectionRef = Firebase.firestore.collection("posts")

    private var onPostDeletedListener : ((String, ChangesType ) -> Unit)? = null

    fun addOnPostArchivedChangedListener(listener :  (String, ChangesType ) -> Unit){
        onPostDeletedListener = listener
    }

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


    suspend fun getPostComments(postId: String) = withContext(Dispatchers.IO){
        collectionRef.document(postId).collection("comments").orderBy("createdAt", Query.Direction.DESCENDING).get(Source.SERVER).await().mapNotNull {
            CommentWrapper.fromSnapshot(it).let{ wrapper ->
                UserRepository.getDoc(wrapper.userId)?.let { userWrapper ->
                    Comment(wrapper, userWrapper)
                }
            }
        }
    }

    suspend fun addComment(postId : String, content: String) = withContext(Dispatchers.IO){
        collectionRef.document(postId).collection("comments").add(
            mapOf(
                "userId" to AuthProvider.authUser()?.uid.toString(),
                "content" to content,
                "createdAt" to Date()
            )
        )
    }

    suspend fun deletePost(postId : String) = withContext(Dispatchers.IO) {
        Firebase.functions.getHttpsCallable("posts-delete").call(mapOf("postId" to postId)).await()
        onPostDeletedListener?.invoke(postId, ChangesType.DELETED)
    }
}