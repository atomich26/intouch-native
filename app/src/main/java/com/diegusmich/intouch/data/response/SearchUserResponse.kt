package com.diegusmich.intouch.data.response

import com.diegusmich.intouch.data.model.UserPreview

class SearchUserResponse : HttpsCallableResponseParser<List<UserPreview>> {
    override fun parse(data: Any): List<UserPreview> {
        return (data as List<HashMap<String, Any>>).map {
            UserPreview(it["id"].toString(), it["name"].toString(), it["username"].toString(), it["img"] as String?)
        }
    }
}