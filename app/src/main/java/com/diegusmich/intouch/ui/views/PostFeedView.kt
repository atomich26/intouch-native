package com.diegusmich.intouch.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import com.diegusmich.intouch.R

class PostFeedView(ctx: Context, attrs: AttributeSet) : LinearLayoutCompat(ctx, attrs) {

    init{
        inflate(ctx, R.layout.post_feed_preview, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }
}