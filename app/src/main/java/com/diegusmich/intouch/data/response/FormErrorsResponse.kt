package com.diegusmich.intouch.data.response

object FormErrorsResponse : HttpsCallableResponseParser<Map<String, String>> {

    override fun parse(data: Any): Map<String, String> {
        val typed = data as List<HashMap<String, String>>
        return typed.associate { Pair(it["field"].toString(), it["message"].toString()) }
    }
}