package com.diegusmich.intouch.data.dto

import com.google.firebase.firestore.DocumentSnapshot

data class FriendshipDTO(
    val id: String,
    val actor: String,
    val notifier: String,
    val fromCache: Boolean
){
    companion object Factory : SnapshotDeserializator<FriendshipDTO>{
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): FriendshipDTO {
            return FriendshipDTO(
                id = documentSnapshot.id,
                actor = documentSnapshot.getString("actor")!!,
                notifier = documentSnapshot.getString("notifier")!!,
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}