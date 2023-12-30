package com.diegusmich.intouch.data.domain

import com.diegusmich.intouch.data.wrapper.CommentWrapper
import com.diegusmich.intouch.data.wrapper.UserWrapper
import java.util.Date

data class Comment(
    val id: String,
    val userInfo: User.Preview,
    val content: String,
    val createdAt: Date
) {
    constructor(wrapper: CommentWrapper, userWrapper: UserWrapper) : this(
        id = wrapper.id,
        userInfo = User.Preview(userWrapper),
        content = wrapper.content,
        createdAt = wrapper.createdAt
    )
}