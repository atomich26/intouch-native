package com.diegusmich.intouch.data.wrapper

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class EventReviewWrapper(
    val id: String,
    val userId: String,
    val content: String,
    val liked: Boolean,
    val createdAt: Date
){
    companion object Factory: SnapshotDeserializator<EventReviewWrapper>{
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): EventReviewWrapper {
            return EventReviewWrapper(
                id = documentSnapshot.id,
                userId = documentSnapshot.getString("userId")!!,
                content = documentSnapshot.getString("content") ?: "",
                liked = documentSnapshot.getBoolean("liked")!!,
                createdAt = documentSnapshot.getTimestamp("createdAt")?.toDate()!!
            )
        }
    }
}