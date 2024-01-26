package com.diegusmich.intouch.data.wrapper

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class CommentWrapper(val id: String, val userId: String, val content: String, val createdAt: Date){
    companion object Factory : SnapshotDeserializator<CommentWrapper>{
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): CommentWrapper {
            return CommentWrapper(
                id = documentSnapshot.id,
                userId = documentSnapshot.getString("userId")!!,
                content = documentSnapshot.getString("content")!!,
                createdAt = documentSnapshot.getDate("createdAt")!!
            )
        }
    }
}