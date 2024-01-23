package com.diegusmich.intouch.data.response

import com.google.firebase.functions.HttpsCallableResult

class SendFriendshipRequestResponse(result: HttpsCallableResult) :
    HttpsCallableResponseParser<String?>() {

    val requestId = parse(result.data)

    override fun parse(data: Any?): String? {
        return with(data as HashMap<String, Any?>) {
            data["requestId"] as String?
        }
    }
}