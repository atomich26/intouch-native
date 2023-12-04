package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.SnapshotDeserializator
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

abstract class FirestoreCollection<T, F : SnapshotDeserializator<T>>(factoryClazz : Class<F>) {

    abstract val collectionRef: CollectionReference
    protected val factory: F = factoryClazz.getDeclaredConstructor().newInstance()

    suspend fun getDoc(id: String, source: Source = Source.DEFAULT): T? = withContext(Dispatchers.IO) {
        val doc = collectionRef.document(id).get(source).await()

        if (!doc.exists())
            return@withContext null

        factory.fromSnapshot(doc)
    }

    suspend fun allDocs(source: Source = Source.DEFAULT): List<T> = withContext(Dispatchers.IO){
        collectionRef.get(source).await().map {
            factory.fromSnapshot(it)
        }
    }

    suspend fun withQuery(source : Source = Source.DEFAULT, query : (CollectionReference) -> Query) = withContext(Dispatchers.IO){
        query(collectionRef).get(source).await().map {
            factory.fromSnapshot(it)
        }
    }
}