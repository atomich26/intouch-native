package com.diegusmich.intouch.data.wrapper

import com.google.firebase.firestore.DocumentSnapshot

data class CategoryWrapper(val id: String, val name: String, val cover: String, val fromCache : Boolean){

    companion object Factory : SnapshotDeserializator<CategoryWrapper> {
        override fun fromSnapshot(documentSnapshot: DocumentSnapshot): CategoryWrapper {
            return CategoryWrapper(
                id = documentSnapshot.id,
                name = documentSnapshot.getString("name")!!,
                cover = documentSnapshot.getString("cover")!!,
                fromCache = documentSnapshot.metadata.isFromCache
            )
        }
    }
}