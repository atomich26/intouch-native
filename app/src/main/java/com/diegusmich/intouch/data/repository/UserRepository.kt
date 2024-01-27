package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.data.wrapper.UserWrapper
import com.diegusmich.intouch.data.response.SearchCallableResponse
import com.diegusmich.intouch.providers.AuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object UserRepository : FirestoreCollection<UserWrapper, UserWrapper.Factory>(UserWrapper.Factory::class.java) {
    override val collectionRef = Firebase.firestore.collection("users")

    suspend fun userProfile(userId: String) = withContext(Dispatchers.IO) {
        getDoc(userId)?.let {
            val friendship = FriendshipRepository.getFriendshipWith(userId)
            val categories = it.preferences.mapNotNull { catId ->
                CategoryRepository.get(catId)
            }
            User.Profile(it, categories, friendship)
        }
    }

    suspend fun search(query: String) = withContext(Dispatchers.IO) {
        Firebase.functions.getHttpsCallable("users-search").call(mapOf("query" to query)).await()
            .let {
                SearchCallableResponse(it).matches.mapNotNull { userId ->
                    getDoc(userId)?.let {wrapper ->
                        User.Preview(wrapper)
                    }
                }
            }
    }

    suspend fun saveAuthUserPreferences(data: List<String>) = withContext(Dispatchers.IO){
        Firebase.firestore.runTransaction {
            AuthProvider.authUser()?.uid?.let{ uid ->
                it.update(collectionRef.document(uid), "preferences", data)
            }
        }.await()
    }

    suspend fun changeProfileImg(imageName: String) = withContext(Dispatchers.IO){
        Firebase.functions.getHttpsCallable("users-editImage").call(mapOf("img" to imageName)).await()
        true
    }

    suspend fun removeProfileImage() = withContext(Dispatchers.IO){
        Firebase.functions.getHttpsCallable("users-removeImage").call().await()
        true
    }

    suspend fun upsertUser(data : Map<String, Any?>) : HttpsCallableResult = withContext(Dispatchers.IO){
        Firebase.functions.getHttpsCallable("users-upsert").call(data).await()
    }

    suspend fun userFriends(id: String): List<User.Preview> = withContext(Dispatchers.IO) {
        getDoc(id)?.friends?.mapNotNull { friendId ->
            getDoc(friendId)?.let { User.Preview(it)}
        } ?: mutableListOf()
    }
}