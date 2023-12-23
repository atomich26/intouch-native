package com.diegusmich.intouch.data.response

import com.google.firebase.functions.HttpsCallableResult

@Suppress("UNCHECKED_CAST")
class SearchCallableResponse(result: HttpsCallableResult) : HttpsCallableResponseParser<List<String>>() {

    val matches : List<String> = parse(result.data)

    override fun parse(data: Any?): List<String> {
        return if(data == null)
            mutableListOf()
        else
            data as List<String>
    }
}