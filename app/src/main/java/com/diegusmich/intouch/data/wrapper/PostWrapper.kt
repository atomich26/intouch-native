package com.diegusmich.intouch.data.wrapper

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

@Suppress("UNCHECKED_CAST")
data class PostWrapper(
    val id: String,
    val eventId: String,
    val userId: String,
    val description: String,
    val album: List<String>,
    val createdAt: Date,
    val fromCache: Boolean
) {
    companion object Factory : SnapshotDeserializator<PostWrapper> {
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): PostWrapper {
            return PostWrapper(
                id = documentSnapshot.id,
                eventId = documentSnapshot.getString("eventId")!!,
                userId = documentSnapshot.getString("userId")!!,
                description = documentSnapshot.getString("description") ?: "",
                album = documentSnapshot.get("album") as List<String>,
                createdAt = documentSnapshot.getTimestamp("createdAt")!!.toDate(),
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}