package com.diegusmich.intouch.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import com.diegusmich.intouch.R

class PostFeedView(ctx: Context) : LinearLayoutCompat(ctx) {

    constructor(ctx: Context, attrs: AttributeSet) : this(ctx)

    init{
        inflate(ctx, R.layout.post_feed_preview, this)
    }

    fun setViewedState(state: Boolean){
        val colorId = if(state) R.color.md_theme_light_outline else R.color.seed
        findViewById<GlideImageView>(R.id.postFeedPreviewUserAvatar).setStrokeColorResource(colorId)
    }
}