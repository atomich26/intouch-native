package com.diegusmich.intouch.data.response

import com.diegusmich.intouch.data.model.Category

object CategoryListResponse : HttpsCallableResponseParser<List<Category>>{

    override fun parse(data : Any): List<Category> {
        return (data as List<HashMap<String, Any>>).map {
            Category(it["id"].toString(), it["name"].toString(), it["cover"].toString())
        }
    }
}