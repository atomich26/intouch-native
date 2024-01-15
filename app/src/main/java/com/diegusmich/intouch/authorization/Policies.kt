package com.diegusmich.intouch.authorization

import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.providers.AuthProvider
import java.util.Date

object Policies {

    fun canEditEvent(event: Event.Full) : Boolean{
        return event.userInfo.id == AuthProvider.authUser()?.uid && event.startAt.time > Date().time
    }

    fun canEditPost(post: Post.Full) : Boolean{
        return post.userInfo.id == AuthProvider.authUser()?.uid
    }
}