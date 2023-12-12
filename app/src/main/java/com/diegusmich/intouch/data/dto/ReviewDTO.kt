package com.diegusmich.intouch.data.dto

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class ReviewDTO(
    val id: String,
    val userId: String,
    val content: String,
    val liked: Boolean,
    val createdAt: Date
){
    companion object Factory: SnapshotDeserializator<ReviewDTO>{
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): ReviewDTO {
            return ReviewDTO(
                id = documentSnapshot.id,
                userId = documentSnapshot.getString("userId")!!,
                content = documentSnapshot.getString("content") ?: "",
                liked = documentSnapshot.getBoolean("liked")!!,
                createdAt = documentSnapshot.getTimestamp("createdAt")?.toDate()!!
            )
        }
    }
}