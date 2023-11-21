package com.diegusmich.intouch.data.model

import java.util.Date

data class User(
    val id: String,
    val name: String,
    val username: String,
    val birthdate: Date,
    val biography: String,
    val isAuthenticated: Boolean,
    val isFriend: Boolean,
    val friends : List<String>?,
    val joined : List<String>?,
    val created : List<String>?,
) {
    companion object : ModelFactory<User> {
        override fun fromMap(data: HashMap<String, Any?>): User {
           return User(
               id = data["id"].toString(),
               name = data["name"].toString(),
               username = data["username"].toString(),
               birthdate = Date(data["birthdate"] as Long),
               biography = data["biography"].toString(),
               isAuthenticated = data["isAuth"] as Boolean,
               isFriend = data["isFriend"] as Boolean,
               friends = data["friends"] as List<String>?,
               joined = data["joined"] as List<String>?,
               created = data["created"] as List<String>?
           )
        }
    }
}