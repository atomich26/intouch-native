package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.UserDTO
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.data.model.UserProfile
import com.diegusmich.intouch.data.response.SearchCallableResponse
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object UserRepository : FirestoreCollection<UserDTO, UserDTO.Factory>(UserDTO.Factory::class.java) {
    override val collectionRef = Firebase.firestore.collection("users")

    suspend fun userProfile(userId: String) = withContext(Dispatchers.IO) {
        val userDoc = getDoc(userId)!!
        val archivedPosts = async { PostRepository.archived(userId) }
        val friendship = async { FriendshipRepository.getFriendship(userId) }
        UserProfile(
            id = userDoc.id,
            isAuth = Firebase.auth.currentUser?.uid!! == userId,
            name = userDoc.name,
            username = userDoc.username,
            biography = userDoc.biography,
            img = userDoc.img,
            friends = userDoc.friends.size,
            joined = userDoc.joined.size,
            created = userDoc.created.size,
            friendship = friendship.await(),
            preferences = listOf(),
            archivedPosts = archivedPosts.await()
        )
    }

    suspend fun searchUser(query: String) = withContext(Dispatchers.IO) {
        Firebase.functions.getHttpsCallable("users-search").call(mapOf("query" to query)).await().let{
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