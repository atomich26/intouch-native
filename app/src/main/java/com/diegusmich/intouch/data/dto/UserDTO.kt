package com.diegusmich.intouch.data.dto

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

@Suppress("UNCHECKED_CAST")
data class UserDTO(
    val id: String,
    val name: String,
    val username: String,
    val birthdate: Date,
    val biography: String,
    val img: String,
    val preferences: List<String>,
    val friends: List<String>,
    val joined: List<String>,
    val created: List<String>,
    val fromCache: Boolean
){
    companion object Factory : SnapshotDeserializator<UserDTO> {
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): UserDTO {
            return UserDTO(
                id = documentSnapshot.id,
                name = documentSnapshot.getString("name")!!,
                username = documentSnapshot.getString("username")!!,
                birthdate = documentSnapshot.getTimestamp("birthdate")!!.toDate(),
                biography = documentSnapshot.getString("biography") ?: "",
                img = documentSnapshot.getString("img") ?: "",
                preferences = documentSnapshot.get("preferences") as List<String>,
                friends = documentSnapshot.get("friends") as List<String>,
                joined = documentSnapshot.get("joined") as List<String>,
                created = documentSnapshot.get("created") as List<String>,
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}