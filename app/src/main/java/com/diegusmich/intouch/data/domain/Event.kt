package com.diegusmich.intouch.data.domain

import com.diegusmich.intouch.data.wrapper.CategoryWrapper
import com.diegusmich.intouch.data.wrapper.EventWrapper
import com.diegusmich.intouch.data.wrapper.UserWrapper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.Date

sealed interface Event{
    data class Full(
        val id: String,
        val name: String,
        val cover: String,
        val startAt: Date,
        val categoryInfo : Category,
        val userInfo: User.Preview,
        val description: String,
        val available: Int,
        val address: String,
        val city: String,
        val geo: GeoPoint,
        val restricted: Boolean,
        val canEdit: Boolean
    ) : Event{
        constructor(eventWrapper: EventWrapper, userWrapper: UserWrapper, categoryWrapper: CategoryWrapper) : this(
            id = eventWrapper.id,
            name = eventWrapper.name,
            cover = eventWrapper.cover,
            startAt = eventWrapper.startAt,
            categoryInfo = Category(categoryWrapper),
            userInfo = User.Preview(userWrapper),
            description = eventWrapper.description,
            available = eventWrapper.available,
            address = eventWrapper.address,
            city = eventWrapper.city,
            geo = eventWrapper.geo,
            restricted = eventWrapper.restricted,
            canEdit = Firebase.auth.currentUser?.uid == userWrapper.id
        )
    }

    data class Preview(
        val id: String,
        val name: String,
        val cover: String,
        val city: String,
        val categoryInfo: Category,
        val startAt: Date,
    ) : Event{
        constructor(eventWrapper: EventWrapper, categoryWrapper: CategoryWrapper) : this(
            id = eventWrapper.id,
            name = eventWrapper.name,
            cover = eventWrapper.cover,
            categoryInfo = Category(categoryWrapper),
            city = eventWrapper.city,
            startAt = eventWrapper.startAt
        )
    }

    data class Info(
        val id: String,
        val name: String
    ) : Event{
        constructor(wrapper: EventWrapper) : this(
            id = wrapper.id,
            name = wrapper.name
        )
    }
}