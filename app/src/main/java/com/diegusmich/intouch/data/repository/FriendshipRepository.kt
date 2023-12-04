package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.FriendshipDTO
import com.diegusmich.intouch.data.model.Friendship
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FriendshipRepository :
    FirestoreCollection<FriendshipDTO, FriendshipDTO.Factory>(FriendshipDTO.Factory::class.java) {
    override val collectionRef: CollectionReference =
        Firebase.firestore.collection("friendship_requests")

    suspend fun getFriendship(userId: String) = withContext(Dispatchers.IO) {
        val authId = Firebase.auth.currentUser?.uid!!

        if(authId == userId)
            return@withContext Friendship(Friendship.Status.AUTH)

        val authUser = UserRepository.getDoc(authId)!!

        if (authUser.friends.contains(userId))
            return@withContext Friendship(Friendship.Status.FRIEND)

        val related = listOf(authId, userId)
        val queryResults =
            collectionRef.whereIn("actor", related).whereIn("notifier", related).get().await()

        if (queryResults.documents.isEmpty())
            return@withContext Friendship(Friendship.Status.NONE)

        val request = FriendshipDTO.fromSnapshot(queryResults.documents[0])

        return@withContext Friendship(
            Friendship.Status.PENDING(
                requestId = request.id,
                isActor = authId == request.actor
            )
        )
    }
}