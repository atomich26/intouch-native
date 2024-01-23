package com.diegusmich.intouch.data.domain

import com.diegusmich.intouch.data.wrapper.FriendshipRequestWrapper
import com.diegusmich.intouch.data.wrapper.UserWrapper
import java.util.Date

data class FriendshipRequest(val id: String, val actor: User.Preview, val createdAt: Date) {
    constructor(wrapper: FriendshipRequestWrapper, userWrapper: UserWrapper) : this(
        id = wrapper.id,
        actor = User.Preview(userWrapper),
        createdAt = wrapper.createdAt
    )
}