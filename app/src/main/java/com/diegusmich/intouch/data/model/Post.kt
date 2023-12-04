package com.diegusmich.intouch.data.model

import java.util.Date

data class Post(
    val id: String,
    val description: String,
    val album: List<String>,
    val createdAt: Date,
    val userInfo: UserPreview,
    val eventInfo: EventPreview
)