package com.diegusmich.intouch.data.domain

import com.diegusmich.intouch.data.wrapper.CategoryWrapper

data class Category(val id: String, val name: String, val cover: String) {
    constructor(wrapper: CategoryWrapper) : this(
        id = wrapper.id,
        name = wrapper.name,
        cover = wrapper.cover
    )
}