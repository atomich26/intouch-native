package com.diegusmich.intouch.data.parser

interface HttpsCallableResponseParser<T> {
    fun parse(data: Any) : T
}