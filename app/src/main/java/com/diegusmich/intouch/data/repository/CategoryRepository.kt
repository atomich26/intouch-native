package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.wrapper.CategoryWrapper
import com.diegusmich.intouch.data.domain.Category
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CategoryRepository :
    FirestoreCollection<CategoryWrapper, CategoryWrapper.Factory>(CategoryWrapper.Factory::class.java) {

    override val collectionRef: CollectionReference = Firebase.firestore.collection("categories")

    suspend fun get(id: String) = withContext(Dispatchers.IO) {
        getDoc(id, Source.DEFAULT)?.let {
            Category(it)
        }
    }

    suspend fun getAll() = withContext(Dispatchers.IO) {
        withQuery(Source.DEFAULT) { it.orderBy("name", Query.Direction.ASCENDING) }.map {
            Category(it)
        }
    }
}