package com.diegusmich.intouch.data.domain

import com.diegusmich.intouch.data.wrapper.UserWrapper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

sealed interface User {

    data class Profile(
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
    ) : User {
        constructor(
            userWrapper: UserWrapper,
            preferences: List<Category>,
            friendship: Friendship
        ) : this(
            id = userWrapper.id,
            isAuth = Firebase.auth.currentUser?.uid!! == userWrapper.id,
            name = userWrapper.name,
            username = userWrapper.username,
            biography = userWrapper.biography,
            img = userWrapper.img,
            preferences = preferences,
            friendship = friendship,
            friends = userWrapper.friends.size,
            joined = userWrapper.joined.size,
            created = userWrapper.created.size,
        )
    }

    data class Preview(
        val id: String,
        val isAuth: Boolean,
        val name: String,
        val username: String,
        val img: String
    ) : User {
        constructor(userWrapper: UserWrapper) : this(
            id = userWrapper.id,
            isAuth = Firebase.auth.currentUser?.uid!! == userWrapper.id,
            name = userWrapper.name,
            username = userWrapper.username,
            img = userWrapper.img
        )
    }
}