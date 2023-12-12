package com.diegusmich.intouch.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class EventDTO(
    val id: String,
    val name: String,
    val cover: String,
    val startAt: Date,
    val userId: String,
    val description: String,
    val address: String,
    val city: String,
    val geo: GeoPoint,
    val restricted: Boolean,
    val fromCache: Boolean
){
    companion object Factory : SnapshotDeserializator<EventDTO>{
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): EventDTO {
            return EventDTO(
                id = documentSnapshot.id,
                name = documentSnapshot.getString("name")!!,
                cover = documentSnapshot.getString("cover")!!,
                startAt = documentSnapshot.getTimestamp("startAt")!!.toDate(),
                userId = documentSnapshot.getString("userId")!!,
                description = documentSnapshot.getString("description")!!,
                address = documentSnapshot.getString("address")!!,
                city = documentSnapshot.getString("city")!!,
                geo = documentSnapshot.getGeoPoint("geo")!!,
                restricted = documentSnapshot.getBoolean("restricted")!!,
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}