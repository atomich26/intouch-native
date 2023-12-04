package com.diegusmich.intouch.data.repository

import com.diegusmich.intouch.data.dto.CategoryDTO
import com.diegusmich.intouch.data.model.Category
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CategoryRepository :
    FirestoreCollection<CategoryDTO, CategoryDTO.Factory>(CategoryDTO.Factory::class.java) {
    override val collectionRef: CollectionReference = Firebase.firestore.collection("categories")

    suspend fun getAll() = withContext(Dispatchers.IO) {
        withQuery { it.orderBy("name", Query.Direction.ASCENDING) }.map {
            Category(
                id = it.id,
                name = it.name,
                cover = it.cover
            )
        }
    }
}