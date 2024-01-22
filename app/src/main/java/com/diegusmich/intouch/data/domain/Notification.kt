package com.diegusmich.intouch.data.domain

import com.diegusmich.intouch.data.wrapper.NotificationWrapper
import java.util.Date

data class Notification(
    val id: String,
    val actorId: String,
    val createdAt: Date,
    val seen: Boolean,
    val type: NotificationType
) {

    constructor(wrapper : NotificationWrapper) : this(
        id = wrapper.id,
        actorId = wrapper.actorId,
        createdAt = wrapper.createdAt,
        seen = wrapper.seen,
        type = NotificationType.COMMENT
    )

    sealed interface NotificationType {
        data object COMMENT : NotificationType
        data object EVENT : NotificationType
        data object FRIENDSHIP : NotificationType
    }
}