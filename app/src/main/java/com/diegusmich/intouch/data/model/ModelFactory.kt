package com.diegusmich.intouch.data.model

interface ModelFactory<T> {
    fun fromMap(data: HashMap<String, Any?>) : T
}