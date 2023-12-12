package com.diegusmich.intouch.data.model

import com.diegusmich.intouch.data.dto.UserDTO

data class UserProfile(
    val id: String,
    val isAuth: Boolean,
    val name: String,
    val username: String,
    val biography: String,
    val img: String,
    val preferences: List<Category>,
    val friends: Int,
    val joined: Int,
    val created: Int,
    val friendship : Friendship,
    val archivedPosts : List<ArchivedPostPreview>
)