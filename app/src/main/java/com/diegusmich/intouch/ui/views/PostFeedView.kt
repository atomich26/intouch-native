package com.diegusmich.intouch.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import com.diegusmich.intouch.R

class PostFeedView(ctx: Context) : LinearLayoutCompat(ctx) {

    constructor(ctx: Context, attrs: AttributeSet) : this(ctx)

    var viewed: Boolean = false
        set(value){
            field = value

        }

    init{
        inflate(ctx, R.layout.post_feed_preview, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        PostFeedView(context)
    }
}