package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.UserDTO
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.data.model.UserProfile
import com.diegusmich.intouch.data.response.SearchUserResponse
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
    private val server = Firebase.functions

    suspend fun getUserProfile(userId: String) = withContext(Dispatchers.IO) {
        val userDoc = getDoc(userId)!!
        val archivedPosts = async{ PostRepository.archivedPosts(userId)}
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
            archivedPosts = archivedPosts.await()
        )
    }

    suspend fun searchUser(query: String) = withContext(Dispatchers.IO) {
        val response = server.getHttpsCallable("users-search").call(mapOf("query" to query)).await()

        return@withContext SearchUserResponse.parse(response.data!!).map{
            val userDoc = UserRepository.getDoc(it)!!
            UserPreview(
                id = userDoc.id,
                name = userDoc.name,
                username = userDoc.username,
                img = userDoc.img
            )
        }
    }

    suspend fun getUserFriends(id: String) = withContext(Dispatchers.IO) {
        val userDoc = getDoc(id) ?: return@withContext null
        userDoc.friends.map {
            val friendDoc = getDoc(it)!!
            UserPreview(
                id = friendDoc.id,
                name = friendDoc.name,
                username = friendDoc.username,
                img = friendDoc.img
            )
        }
    }
}