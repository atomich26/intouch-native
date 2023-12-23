package com.diegusmich.intouch.data.wrapper

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class EventWrapper(
    val id: String,
    val name: String,
    val cover: String,
    val startAt: Date,
    val userId: String,
    val description: String,
    val address: String,
    val available: Int,
    val city: String,
    val categoryId: String,
    val geo: GeoPoint,
    val restricted: Boolean,
    val fromCache: Boolean
){
    companion object Factory : SnapshotDeserializator<EventWrapper>{
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): EventWrapper {
            return EventWrapper(
                id = documentSnapshot.id,
                name = documentSnapshot.getString("name")!!,
                cover = documentSnapshot.getString("cover")!!,
                startAt = documentSnapshot.getTimestamp("startAt")!!.toDate(),
                userId = documentSnapshot.getString("userId")!!,
                description = documentSnapshot.getString("description")!!,
                address = documentSnapshot.getString("address")!!,
                available = documentSnapshot.get("available") as Int,
                categoryId = documentSnapshot.getString("categoryId")!!,
                city = documentSnapshot.getString("city")!!,
                geo = documentSnapshot.getGeoPoint("geo")!!,
                restricted = documentSnapshot.getBoolean("restricted")!!,
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}