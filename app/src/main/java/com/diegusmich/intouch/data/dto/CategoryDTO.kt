package com.diegusmich.intouch.data.dto

import com.google.firebase.firestore.DocumentSnapshot

data class CategoryDTO(val id: String, val name: String, val cover: String, val fromCache : Boolean){

    companion object Factory : SnapshotDeserializator<CategoryDTO> {
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): CategoryDTO {
            return CategoryDTO(
                id = documentSnapshot.id,
                name = documentSnapshot.getString("name")!!,
                cover = documentSnapshot.getString("cover")!!,
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}