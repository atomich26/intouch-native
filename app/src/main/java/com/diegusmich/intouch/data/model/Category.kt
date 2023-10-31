package com.diegusmich.intouch.data.model

data class Category(
    val id: String? = null,
    val name: String? = null,
    val cover: String? = null
){
    companion object : ModelFactory<Category>{
        override fun fromMap(data: HashMap<String, Any?>): Category {
            return Category(
                id = data["id"].toString(),
                name = data["name"].toString(),
                cover = data["cover"].toString())
        }
    }
}