package com.diegusmich.intouch.data.domain

import android.location.Location
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
        val endAt: Date?,
        val categoryInfo : Category,
        val userInfo: User.Preview,
        val description: String,
        val available: Int,
        val address: String,
        val city: String,
        val geo: Location,
        val restricted: Boolean,
    ) : Event{
        constructor(eventWrapper: EventWrapper, userWrapper: UserWrapper, categoryWrapper: CategoryWrapper) : this(
            id = eventWrapper.id,
            name = eventWrapper.name,
            cover = eventWrapper.cover,
            startAt = eventWrapper.startAt,
            endAt = eventWrapper.endAt,
            categoryInfo = Category(categoryWrapper),
            userInfo = User.Preview(userWrapper),
            description = eventWrapper.description,
            available = eventWrapper.available,
            address = eventWrapper.address,
            city = eventWrapper.city,
            geo = Location("").apply {
                latitude = eventWrapper.geo.latitude
                longitude = eventWrapper.geo.longitude
            },
            restricted = eventWrapper.restricted
        )
    }

    data class FeedPreview(
        val id: String,
        val name: String,
        val cover: String,
        val userInfo: User.Preview,
        val city: String,
        val geo: Location,
        val startAt: Date
    ) : Event{
        constructor(eventWrapper: EventWrapper, userWrapper: UserWrapper) : this(
            id = eventWrapper.id,
            name = eventWrapper.name,
            cover = eventWrapper.cover,
            userInfo = User.Preview(userWrapper),
            geo = Location("").apply {
                latitude = eventWrapper.geo.latitude
                longitude = eventWrapper.geo.longitude
            },
            city = eventWrapper.city,
            startAt = eventWrapper.startAt
        )
    }
    data class Preview(
        val id: String,
        val name: String,
        val cover: String,
        val city: String,
        val geo: Location,
        val categoryInfo: Category,
        val startAt: Date,
    ) : Event{
        constructor(eventWrapper: EventWrapper, categoryWrapper: CategoryWrapper) : this(
            id = eventWrapper.id,
            name = eventWrapper.name,
            cover = eventWrapper.cover,
            geo = Location("").apply {
                latitude = eventWrapper.geo.latitude
                longitude = eventWrapper.geo.longitude
            },
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

    sealed interface STATE {
        data object ACTIVE_AVAILABLE : STATE
        data object ACTIVE_NOT_AVAILABLE : STATE
        data object ACTIVE_JOINED : STATE
        data object ACTIVE_CLOSED : STATE
        data object TERMINATED_JOINED: STATE
        data object TERMINATED_NOT_JOINED : STATE
    }
}
