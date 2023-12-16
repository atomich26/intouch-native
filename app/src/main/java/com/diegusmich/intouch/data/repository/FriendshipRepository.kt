package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.FriendshipDTO
import com.diegusmich.intouch.data.model.Friendship
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FriendshipRepository :
    FirestoreCollection<FriendshipDTO, FriendshipDTO.Factory>(FriendshipDTO.Factory::class.java) {
    override val collectionRef: CollectionReference =
        Firebase.firestore.collection("friendship_requests")

    suspend fun getFriendshipWith(userId: String) = withContext(Dispatchers.IO) {
        val authId = Firebase.auth.currentUser?.uid!!

        if (authId == userId)
            return@withContext Friendship(Friendship.Status.AUTH)

        val authUser = UserRepository.getDoc(authId)!!

        if (authUser.friends.contains(userId))
            return@withContext Friendship(Friendship.Status.FRIEND)

        val related = listOf(authId, userId)
        val queryResults = withQuery {
            collectionRef.whereIn("actor", related).whereIn("notifier", related)
        }

        if (queryResults.isEmpty())
            return@withContext Friendship(Friendship.Status.NONE)

        return@withContext with(queryResults[0]) {
            Friendship(
                Friendship.Status.PENDING(
                    requestId = this.id,
                    isActor = authId == this.actor
                )
            )
        }
    }
}