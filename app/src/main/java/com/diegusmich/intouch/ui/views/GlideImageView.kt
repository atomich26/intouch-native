package com.diegusmich.intouch.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.diegusmich.intouch.R
import com.diegusmich.intouch.module.GlideApp
import com.diegusmich.intouch.module.GlideRequest
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.storage.StorageReference

@SuppressLint("CheckResult")
class GlideImageView(ctx: Context, attrs: AttributeSet) : ShapeableImageView(ctx, attrs) {

    private lateinit var _glideRequest: GlideRequest<Drawable>
    private val isCircleCropped : Boolean
    private val placeholderDrawable : Int

    init {
        ctx.theme.obtainStyledAttributes(attrs, R.styleable.GlideImageView, 0, 0).apply {
            try {
                isCircleCropped = getBoolean(R.styleable.GlideImageView_circleCrop, false)
                placeholderDrawable = (getResourceId(
                    R.styleable.GlideImageView_placeholder,
                    R.drawable.glide_image_placeholder_blank
                ))
            } finally {
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        _glideRequest = GlideApp.with(context).load(this@GlideImageView.drawable).apply {
            placeholder(placeholderDrawable)

            if (isCircleCropped)
                circleCrop()

            diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            into(this@GlideImageView)
        }
    }

    fun load(url: String) {
        if(url.isNotBlank())
            _glideRequest.load(url).into(this)
    }

    fun load(imgReference: StorageReference) {
        _glideRequest.load(imgReference).into(this@GlideImageView)
    }

    fun clear() {
        GlideApp.with(context).clear(this)
    }
}