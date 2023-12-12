package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.EventDTO
import com.diegusmich.intouch.data.model.Event
import com.diegusmich.intouch.data.model.UserPreview
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository : FirestoreCollection<EventDTO, EventDTO.Factory>(EventDTO.Factory::class.java) {
    override val collectionRef: CollectionReference = Firebase.firestore.collection("events")

    suspend fun getEvent(id: String) = withContext(Dispatchers.IO){
        val eventDoc = getDoc(id) ?: return@withContext null
        val userDoc = UserRepository.getDoc(eventDoc.userId)!!

        Event(
            id = eventDoc.id,
            name = eventDoc.name,
            cover = eventDoc.cover,
            address = eventDoc.address,
            city = eventDoc.city,
            user = UserPreview.fromUserDTO(userDoc),
            description = eventDoc.description,
            geo = eventDoc.geo,
            startAt = eventDoc.startAt,
            restricted = eventDoc.restricted
        )
    }
}