package com.diegusmich.intouch.data.wrapper

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

@Suppress("UNCHECKED_CAST")
data class UserWrapper(
    val id: String,
    val name: String,
    val username: String,
    val birthdate: Date,
    val biography: String,
    val img: String,
    val distanceRange: Int,
    val preferences: List<String>,
    val friends: List<String>,
    val joined: List<String>,
    val created: List<String>,
    val fromCache: Boolean
){
    companion object Factory : SnapshotDeserializator<UserWrapper> {
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): UserWrapper {
            return UserWrapper(
                id = documentSnapshot.id,
                name = documentSnapshot.getString("name")!!,
                username = documentSnapshot.getString("username")!!,
                birthdate = Date(documentSnapshot.getTimestamp("birthdate")!!.seconds * 1000),
                biography = documentSnapshot.getString("biography") ?: "",
                img = documentSnapshot.getString("img") ?: "",
                preferences = documentSnapshot.get("preferences") as List<String>,
                friends = documentSnapshot.get("friends") as List<String>,
                joined = documentSnapshot.get("joined") as List<String>,
                created = documentSnapshot.get("created") as List<String>,
                distanceRange = documentSnapshot.getLong("distanceRange")?.toInt() ?: 0,
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}