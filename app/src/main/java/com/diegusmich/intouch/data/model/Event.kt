package com.diegusmich.intouch.data.model

import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Event(
    val id: String,
    val name: String,
    val cover: String,
    val startAt: Date,
    val user: UserPreview,
    val description: String,
    val address: String,
    val city: String,
    val geo: GeoPoint,
    val restricted: Boolean,
)