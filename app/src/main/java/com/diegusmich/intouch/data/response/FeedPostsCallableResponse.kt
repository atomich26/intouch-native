package com.diegusmich.intouch.data.response

import com.diegusmich.intouch.data.wrapper.FeedPostWrapper
import com.google.firebase.Timestamp
import com.google.firebase.functions.HttpsCallableResult
import java.util.Date

class FeedPostsCallableResponse(result: HttpsCallableResult): HttpsCallableResponseParser<List<FeedPostWrapper>>() {

    val feedPosts: List<FeedPostWrapper> = parse(result.data)

    override fun parse(data: Any?): List<FeedPostWrapper> {
        return with(data as List<HashMap<String, Any?>>){
            this.map {
                FeedPostWrapper(
                    id = it["id"] as String,
                    userId = it["userId"] as String,
                    createdAt = Date(it["createdAt"] as Long)
                )
            }
        }
    }
}