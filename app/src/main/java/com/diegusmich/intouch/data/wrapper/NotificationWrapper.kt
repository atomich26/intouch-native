package com.diegusmich.intouch.data.wrapper

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class NotificationWrapper(
    val id: String,
    val actorId: String,
    val createdAt: Date,
    val seen: Boolean,
    val type: String
) {
    companion object Factory : SnapshotDeserializator<NotificationWrapper> {
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): NotificationWrapper {
            return NotificationWrapper(
                id = documentSnapshot.id,
                actorId = documentSnapshot.getString("actorId")!!,
                createdAt = Date(documentSnapshot.getTimestamp("createdAt")!!.seconds * 1000),
                seen = documentSnapshot.getBoolean("seen") ?: false,
                type = documentSnapshot.getString("type")!!
            )
        }
    }
}