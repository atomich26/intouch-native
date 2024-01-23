package com.diegusmich.intouch.data.wrapper

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class FriendshipRequestWrapper(
    val id: String,
    val actor: String,
    val notifier: String,
    val createdAt : Date,
    val fromCache: Boolean
){
    companion object Factory : SnapshotDeserializator<FriendshipRequestWrapper>{
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): FriendshipRequestWrapper {
            return FriendshipRequestWrapper(
                id = documentSnapshot.id,
                actor = documentSnapshot.getString("actor")!!,
                notifier = documentSnapshot.getString("notifier")!!,
                createdAt = Date(documentSnapshot.getTimestamp("createdAt")?.seconds!! * 1000),
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}