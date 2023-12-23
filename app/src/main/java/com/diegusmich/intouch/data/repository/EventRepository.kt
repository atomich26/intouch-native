package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.wrapper.EventWrapper
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.domain.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EventRepository : FirestoreCollection<EventWrapper, EventWrapper.Factory>(EventWrapper.Factory::class.java) {
    override val collectionRef: CollectionReference = Firebase.firestore.collection("events")

    suspend fun event(id: String) = withContext(Dispatchers.IO){
        getDoc(id)?.let { eventWrapper ->
            UserRepository.getDoc(eventWrapper.userId)?.let { userWrapper ->
                Event.Full(eventWrapper, userWrapper)
            }
        }
    }

    suspend fun createdBy(userId: String) = withContext(Dispatchers.IO){
        UserRepository.getDoc(userId)?.let {
            it.created.mapNotNull { eventId ->
                getDoc(eventId)?.let { eventData ->
                    Event.Preview(eventData)
                }
            }
        } ?: mutableListOf()
    }


    suspend fun joinedBy(userId: String) = withContext(Dispatchers.IO){
        UserRepository.getDoc(userId)?.let {
            it.joined.mapNotNull { eventId ->
                getDoc(eventId)?.let { eventData ->
                    Event.Preview(eventData)
                }
            }
        } ?: mutableListOf()
    }

    suspend fun search(query: String) = withContext(Dispatchers.IO){
        //TODO("Da implementare")
    }
}