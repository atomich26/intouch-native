package com.diegusmich.intouch.authorization

import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.providers.AuthProvider
import java.util.Date

object Policies {

    fun canEditEvent(event: Event.Full): Boolean {
        return event.userInfo.id == AuthProvider.authUser()?.uid && event.startAt.time > Date().time
    }

    fun canDeletePost(post: Post.Full): Boolean {
        return post.userInfo.id == AuthProvider.authUser()?.uid
    }

    fun canEventState(
        event: Event.Full,
        attendees: List<User.Preview>,
        isFriend: Boolean
    ): Event.STATE {
        return AuthProvider.authUser()?.uid?.let {
            val alreadyJoined = attendees.any { user -> user.id == it }
            val isAvailable = event.available > 0
            val isRestricted = event.restricted
            val isStarted = event.startAt
            val isTerminated = event.endAt?.let {
                event.endAt < Date()
            } ?: (event.startAt < Date())

            if (!isTerminated) {
                if (!isFriend && isRestricted) {
                    Event.STATE.ACTIVE_CLOSED
                } else if (alreadyJoined) {
                    Event.STATE.ACTIVE_JOINED
                } else if (!isAvailable)
                    Event.STATE.ACTIVE_NOT_AVAILABLE
                else
                    Event.STATE.ACTIVE_AVAILABLE
            } else if (alreadyJoined) {
                Event.STATE.TERMINATED_JOINED
            } else
                Event.STATE.TERMINATED_NOT_JOINED
        } ?: Event.STATE.TERMINATED_NOT_JOINED
    }
}