package com.diegusmich.intouch.data.model

import java.util.Date

data class EventPreview(
    val id: String,
    val name: String,
    val date: Date,
    val city: String,
    val user: UserPreview,
    val img: String
)