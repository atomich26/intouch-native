package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.UserDTO
import com.diegusmich.intouch.data.model.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

object UserRepository : FirestoreCollection<UserDTO, UserDTO.Factory>(UserDTO.Factory::class.java) {
    override val collectionRef = Firebase.firestore.collection("users")

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
}