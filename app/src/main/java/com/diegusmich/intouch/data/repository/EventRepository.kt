package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.data.response.SearchCallableResponse
import com.diegusmich.intouch.data.wrapper.EventWrapper
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

object EventRepository :
    FirestoreCollection<EventWrapper, EventWrapper.Factory>(EventWrapper.Factory::class.java) {
    override val collectionRef: CollectionReference = Firebase.firestore.collection("events")

    suspend fun event(id: String) = withContext(Dispatchers.IO) {
        getDoc(id)?.let { eventWrapper ->
            UserRepository.getDoc(eventWrapper.userId)?.let { userWrapper ->
                CategoryRepository.getDoc(eventWrapper.categoryId)?.let { categoryWrapper ->
                    Event.Full(eventWrapper, userWrapper, categoryWrapper)
                }
            }
        }
    }

    suspend fun createdBy(userId: String) = withContext(Dispatchers.IO) {
        UserRepository.getDoc(userId)?.let { userWrapper ->
            withQuery {
                it.whereIn("__name__", userWrapper.created)
            }.mapNotNull { eventWrapper ->
                CategoryRepository.getDoc(eventWrapper.categoryId)?.let { categoryWrapper ->
                    Event.Preview(eventWrapper, categoryWrapper)
                }
            }.sortedByDescending { it.startAt }
        } ?: mutableListOf()
    }

    suspend fun joinedBy(userId: String) = withContext(Dispatchers.IO) {
        UserRepository.getDoc(userId)?.let { userWrapper ->
            withQuery {
                it.whereIn("__name__", userWrapper.joined)
            }.mapNotNull { eventWrapper ->
                CategoryRepository.getDoc(eventWrapper.categoryId)?.let { categoryWrapper ->
                    Event.Preview(eventWrapper, categoryWrapper)
                }
            }.sortedByDescending { it.startAt }
        } ?: mutableListOf()
    }

    suspend fun selectByCategory(categoryId: String) = withContext(Dispatchers.IO) {
        withQuery {
            it.whereEqualTo("categoryId", categoryId).whereGreaterThan("startAt", Date()).orderBy("startAt", Query.Direction.DESCENDING)
        }.mapNotNull { eventWrapper ->
            CategoryRepository.getDoc(eventWrapper.categoryId)?.let {
                Event.Preview(eventWrapper, it)
            }
        }
    }

    suspend fun search(query: String) = withContext(Dispatchers.IO) {
        Firebase.functions.getHttpsCallable("events-search").call(mapOf("query" to query)).await()
            .let {
                SearchCallableResponse(it).matches.mapNotNull { eventId ->
                    EventRepository.getDoc(eventId)?.let { eventWrapper ->
                        CategoryRepository.getDoc(eventWrapper.categoryId)?.let {categoryWrapper ->
                            Event.Preview(eventWrapper, categoryWrapper)
                        }
                    }
                }
            }
    }
}