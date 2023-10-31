package com.diegusmich.intouch.data.parser

object FormErrorsParser : HttpsCallableResponseParser<Map<String, String>> {

    override fun parse(data: Any): Map<String, String> {
        val typed = data as List<HashMap<String, String>>
        return typed.associate { Pair(it["field"].toString(), it["message"].toString()) }
    }
}