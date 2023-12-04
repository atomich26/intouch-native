package com.diegusmich.intouch.data.dto

import com.google.firebase.firestore.DocumentSnapshot

interface SnapshotDeserializator<T>{
    fun fromSnapshot(documentSnapshot: DocumentSnapshot) : T
}