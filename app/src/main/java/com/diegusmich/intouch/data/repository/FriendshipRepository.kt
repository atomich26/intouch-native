package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.domain.Friendship
import com.diegusmich.intouch.data.domain.FriendshipRequest
import com.diegusmich.intouch.data.wrapper.FriendshipRequestWrapper
import com.diegusmich.intouch.providers.AuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FriendshipRepository :
    FirestoreCollection<FriendshipRequestWrapper, FriendshipRequestWrapper.Factory>(
        FriendshipRequestWrapper.Factory::class.java
    ) {
    override val collectionRef: CollectionReference =
        Firebase.firestore.collection("friendship_requests")

    fun runOnFriendshipRequestsUpdate(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit): ListenerRegistration? {
        return collectionRef.whereEqualTo("notifier", AuthProvider.authUser()?.uid!!)
            .addSnapshotListener { snapshots, e ->
                listener(snapshots, e)
            }
    }

    suspend fun getAllFriendshipWithAuthUser() = withContext(Dispatchers.IO) {
        AuthProvider.authUser()?.uid?.let{ uid ->
            withQuery {
                collectionRef.whereEqualTo("notifier", uid )
            }.mapNotNull { friendshipWrapper ->
                UserRepository.getDoc(friendshipWrapper.actor)?.let { userWrapper ->
                    FriendshipRequest(friendshipWrapper, userWrapper)
                }
            }
        }
    }

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

    suspend fun sendRequest(userId: String): HttpsCallableResult? =
        withContext(Dispatchers.IO) {
            Firebase.functions.getHttpsCallable("friendships-send")
                .call(mapOf("userId" to userId)).await()
        }

    suspend fun handleRequest(requestId: String, confirm: Boolean): HttpsCallableResult? =
        withContext(Dispatchers.IO) {
            Firebase.functions.getHttpsCallable("friendships-handle")
                .call(mapOf("requestId" to requestId, "result" to confirm)).await()
        }

    suspend fun removeFriendship(userId: String): HttpsCallableResult? =
        withContext(Dispatchers.IO) {
            Firebase.functions.getHttpsCallable("friendships-remove")
                .call(mapOf("userId" to userId)).await()
        }
}