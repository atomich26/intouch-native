package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.UserDTO
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.data.model.UserProfile
import com.diegusmich.intouch.data.response.SearchCallableResponse
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object UserRepository : FirestoreCollection<UserDTO, UserDTO.Factory>(UserDTO.Factory::class.java) {
    override val collectionRef = Firebase.firestore.collection("users")

    suspend fun userProfile(userId: String) = withContext(Dispatchers.IO) {
        getDoc(userId)?.let {
            val friendship = FriendshipRepository.getFriendshipWith(userId)
            val categories = it.preferences.mapNotNull { catId ->
                CategoryRepository.get(catId)
            }
            UserProfile.fromDTO(it, categories, friendship)
        }
    }

    suspend fun searchUser(query: String) = withContext(Dispatchers.IO) {
        Firebase.functions.getHttpsCallable("users-search").call(mapOf("query" to query)).await()
            .let {
                SearchCallableResponse(it).matches.mapNotNull { userId ->
                    getDoc(userId)?.let { dto ->
                        UserPreview.fromUserDTO(dto)
                    }
                }
            }
    }

    suspend fun userFriends(id: String): List<UserPreview> = withContext(Dispatchers.IO) {
        getDoc(id)?.friends?.mapNotNull { friendId ->
            getDoc(friendId)?.let { UserPreview.fromUserDTO(it) }
        } ?: mutableListOf()
    }
}