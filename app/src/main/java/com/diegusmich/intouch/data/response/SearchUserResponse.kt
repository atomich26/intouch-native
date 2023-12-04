package com.diegusmich.intouch.data.response

class SearchUserResponse : HttpsCallableResponseParser<List<String>> {
    override fun parse(data: Any): List<String> {
       return data as List<String>
    }
}