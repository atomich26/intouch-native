package com.diegusmich.intouch.data.wrapper

import com.google.firebase.firestore.DocumentSnapshot

data class FriendshipWrapper(
    val id: String,
    val actor: String,
    val notifier: String,
    val fromCache: Boolean
){
    companion object Factory : SnapshotDeserializator<FriendshipWrapper>{
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): FriendshipWrapper {
            return FriendshipWrapper(
                id = documentSnapshot.id,
                actor = documentSnapshot.getString("actor")!!,
                notifier = documentSnapshot.getString("notifier")!!,
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}