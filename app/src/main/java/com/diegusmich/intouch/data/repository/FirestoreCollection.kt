package com.diegusmich.intouch.data.repository

import android.content.pm.ChangedPackages
import com.diegusmich.intouch.data.wrapper.SnapshotDeserializator
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

abstract class FirestoreCollection<T, F : SnapshotDeserializator<T>>(factoryClazz : Class<F>) {

    abstract val collectionRef: CollectionReference
    protected val factory: F = factoryClazz.getDeclaredConstructor().newInstance()

    suspend fun getDoc(id: String, source: Source = Source.SERVER): T? = withContext(Dispatchers.IO) {
        val doc = collectionRef.document(id).get(source).await()

        if (!doc.exists())
            return@withContext null

        factory.fromSnapshot(doc)
    }

    suspend fun allDocs(source: Source = Source.SERVER): List<T> = withContext(Dispatchers.IO){
        collectionRef.get(source).await().map {
            factory.fromSnapshot(it)
        }
    }

    suspend fun withQuery(source : Source = Source.SERVER, query : (CollectionReference) -> Query) = withContext(Dispatchers.IO){
        query(collectionRef).get(source).await().map {
            factory.fromSnapshot(it)
        }
    }


    fun runOnDocumentUpdate(id: String, callback: () -> Unit) : ListenerRegistration{
        return collectionRef.whereEqualTo("__name__", id).addSnapshotListener { snapshots, e ->
            snapshots?.documentChanges?.get(0)?.let {
                if(it.type == DocumentChange.Type.MODIFIED)
                    callback()
            }
        }
    }

    fun runOnCollectionUpdate(callback: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) : ListenerRegistration{
        return collectionRef.addSnapshotListener { q, e ->
            callback(q, e)
        }
    }

    sealed interface ChangesType{
        data object DELETED : ChangesType
        data object ADDED : ChangesType
    }
}