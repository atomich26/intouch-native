package com.diegusmich.intouch.data.repository

import android.location.Location
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.data.response.SearchCallableResponse
import com.diegusmich.intouch.data.wrapper.EventWrapper
import com.diegusmich.intouch.providers.AuthProvider
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.providers.UserLocationProvider
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firestore.v1.StructuredQuery.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
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
            }.sortedBy { it.startAt }
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
            }.sortedBy{ it.startAt }
        } ?: mutableListOf()
    }

    suspend fun selectByCategory(categoryId: String, currentLocation : Location) = withContext(Dispatchers.IO) {
        AuthProvider.authUser()?.uid?.let{
            UserRepository.getDoc(it)?.let{ authWrapper ->
                withQuery { collectionRef ->
                    collectionRef.whereEqualTo("categoryId", categoryId).whereGreaterThan("startAt", Date()).orderBy("startAt", Query.Direction.ASCENDING)
                }.mapNotNull { eventWrapper ->
                    CategoryRepository.getDoc(eventWrapper.categoryId)?.let { catWrapper ->
                        Event.Preview(eventWrapper, catWrapper)
                    }
                }.filter{ eventPreview ->
                    val distance = currentLocation.distanceTo(eventPreview.geo)
                    distance < authWrapper.distanceRange * 1000
                }
            }
        }
    }

    suspend fun feed(currentLocation : Location) = withContext(Dispatchers.IO){
        AuthProvider.authUser()?.uid?.let{
            UserRepository.getDoc(it)?.let{ authWrapper ->
                val userPrefs = authWrapper.preferences

                withQuery { collectionRef ->
                    collectionRef
                        .whereGreaterThan("startAt", Date())
                        .whereIn("categoryId", userPrefs)
                        .orderBy("startAt", Query.Direction.ASCENDING)
                }.filter{ eventWrapper ->
                    eventWrapper.available > 0 && if(eventWrapper.restricted) authWrapper.friends.contains(eventWrapper.userId) else true
                }.filter{ eventWrapper ->
                    eventWrapper.userId != authWrapper.id && !authWrapper.joined.contains(eventWrapper.id)
                }.mapNotNull{ eventWrapper ->
                    UserRepository.getDoc(eventWrapper.userId)?.let{ userWrapper ->
                        Event.FeedPreview(eventWrapper, userWrapper)
                    }
                }.filter{ eventPreview ->
                    val distance = currentLocation.distanceTo(eventPreview.geo)
                    distance < authWrapper.distanceRange * 1000
                }
            }
        }
    }

    suspend fun attendees(eventId: String?) = withContext(Dispatchers.IO){
        eventId?.let{
            val list = collectionRef.document(eventId).collection("attendees").get().await().documents[0].get("users") as List<String>
            list.mapNotNull {
                UserRepository.getDoc(it)?.let{ userWrapper ->
                    User.Preview(userWrapper)
                }
            }
        } ?: mutableListOf()
    }

    suspend fun join(eventId: String, confirm: Boolean) : HttpsCallableResult = withContext(Dispatchers.IO){
        Firebase.functions.getHttpsCallable("events-join").call(mapOf("eventId" to eventId, "join" to confirm)).await()
    }

    suspend fun upsert(data: Map<String, Any>) : HttpsCallableResult = withContext(Dispatchers.IO){
        Firebase.functions.getHttpsCallable("events-upsert").call(data).await()
    }

    suspend fun deleteEvent(eventId: String) : HttpsCallableResult = withContext(Dispatchers.IO){
        Firebase.functions.getHttpsCallable("events-delete").call(mapOf("eventId" to eventId)).await()
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