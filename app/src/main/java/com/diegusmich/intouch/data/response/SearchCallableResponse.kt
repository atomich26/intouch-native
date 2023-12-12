package com.diegusmich.intouch.data.response

import com.google.firebase.functions.HttpsCallableResult

class SearchCallableResponse(result: HttpsCallableResult) : HttpsCallableResponseParser<List<String>>() {

    val matches : List<String>

    init{
        matches = this.parse(result.data)
    }

    override fun parse(data: Any?): List<String> {
        return if(data == null)
            mutableListOf()
        else
            data as List<String>
    }
}