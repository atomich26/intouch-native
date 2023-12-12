package com.diegusmich.intouch.data.model

import com.diegusmich.intouch.data.dto.UserDTO
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class UserPreview(
    val id: String,
    val isAuth: Boolean,
    val name: String,
    val username: String,
    val img: String
){
    companion object {
        fun fromUserDTO(dto: UserDTO) : UserPreview{
            return UserPreview(
                id = dto.id,
                isAuth = Firebase.auth.currentUser?.uid == dto.id,
                name = dto.name,
                username = dto.username,
                img = dto.img
            )
        }
    }
}