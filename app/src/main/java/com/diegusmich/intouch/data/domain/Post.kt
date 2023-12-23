package com.diegusmich.intouch.data.domain

import com.diegusmich.intouch.data.wrapper.EventWrapper
import com.diegusmich.intouch.data.wrapper.FeedPostWrapper
import com.diegusmich.intouch.data.wrapper.PostWrapper
import com.diegusmich.intouch.data.wrapper.UserWrapper
import java.util.Date

sealed interface Post {

    data class Full(
        val id: String,
        val description: String,
        val album: List<String>,
        val createdAt: Date,
        val userInfo: User.Preview,
        val eventInfo: Event.Info
    ) : Post {
        constructor(
            postWrapper: PostWrapper,
            userWrapper: UserWrapper,
            eventWrapper: EventWrapper
        ) : this(
            id = postWrapper.id,
            description = postWrapper.description,
            album = postWrapper.album,
            createdAt = postWrapper.createdAt,
            userInfo = User.Preview(userWrapper),
            eventInfo = Event.Info(eventWrapper)
        )
    }

    data class FeedPreview(
        val id: String,
        val viewed: Boolean,
        val userInfo: User.Preview
    ) : Post {
        constructor(feedPostWrapper: FeedPostWrapper, userWrapper: UserWrapper) : this(
            id = feedPostWrapper.id,
            viewed = feedPostWrapper.viewed,
            userInfo = User.Preview(userWrapper)
        )
    }

    data class ArchivePreview(
        val id: String,
        val thumbnail: String,
        val createdAt: Date
    ) : Post{
        constructor(wrapper: PostWrapper) : this(
            id = wrapper.id,
            thumbnail = wrapper.album[0],
            createdAt = wrapper.createdAt
        )
    }
}