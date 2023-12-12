package com.diegusmich.intouch.data.dto

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

@Suppress("UNCHECKED_CAST")
data class PostDTO(
    val id: String,
    val eventId: String,
    val description: String,
    val album: List<String>,
    val createdAt: Date,
    val fromCache: Boolean
) {
    companion object Factory : SnapshotDeserializator<PostDTO> {
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): PostDTO {
            return PostDTO(
                id = documentSnapshot.id,
                eventId = documentSnapshot.getString("eventId")!!,
                description = documentSnapshot.getString("description") ?: "",
                album = documentSnapshot.get("album") as List<String>,
                createdAt = documentSnapshot.getTimestamp("createdAt")!!.toDate(),
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}