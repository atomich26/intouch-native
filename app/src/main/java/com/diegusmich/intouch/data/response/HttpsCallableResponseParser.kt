package com.diegusmich.intouch.data.response

interface HttpsCallableResponseParser<T>{
    fun parse(data : Any) : T
}