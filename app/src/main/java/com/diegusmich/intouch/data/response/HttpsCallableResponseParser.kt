package com.diegusmich.intouch.data.response

abstract class HttpsCallableResponseParser<T> {
    protected abstract fun parse(data: Any?): T
}