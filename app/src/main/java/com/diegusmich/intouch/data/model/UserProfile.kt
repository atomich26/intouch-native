package com.diegusmich.intouch.data.model

import com.diegusmich.intouch.data.dto.UserDTO
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class UserProfile(
    val id: String,
    val isAuth: Boolean,
    val name: String,
    val username: String,
    val biography: String,
    val img: String,
    val preferences: List<Category>,
    val friendship: Friendship,
    val friends: Int,
    val joined: Int,
    val created: Int,
) {
    companion object {
        fun fromDTO(dto: UserDTO, preferences: List<Category>, friendship: Friendship): UserProfile {
           return UserProfile(
                id = dto.id,
                isAuth = Firebase.auth.currentUser?.uid!! == dto.id,
                name = dto.name,
                username = dto.username,
                biography = dto.biography,
                img = dto.img,
                preferences = preferences,
                friendship = friendship,
                friends = dto.friends.size,
                joined = dto.joined.size,
                created = dto.created.size,
            )
        }
    }
}