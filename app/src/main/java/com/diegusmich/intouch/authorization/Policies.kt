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

    fun canAddPost(event: Event.Full, attendees: List<User.Preview>): Boolean {
        val dateToEvaluate = event.endAt ?: event.startAt
        val authId = AuthProvider.authUser()?.uid

        return dateToEvaluate.before(Date()) && attendees.any { user -> user.id == authId }
    }
}