package com.diegusmich.intouch.data.wrapper

import com.google.firebase.firestore.DocumentSnapshot

interface SnapshotDeserializator<T>{
    fun fromSnapshot(documentSnapshot: DocumentSnapshot) : T
}