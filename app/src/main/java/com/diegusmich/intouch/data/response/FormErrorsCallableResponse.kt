package com.diegusmich.intouch.data.response

class FormErrorsCallableResponse(details: Any?) : HttpsCallableResponseParser<Map<String, String>>(){

    val errors : Map<String, String>

    init{
        errors = this.parse(details)
    }

    override fun parse(data: Any?): Map<String, String> {
        return (data as List<HashMap<String, String>>?)?.associate {
            Pair(it["field"].toString(), it["message"].toString())
        } ?: mapOf()
    }
}