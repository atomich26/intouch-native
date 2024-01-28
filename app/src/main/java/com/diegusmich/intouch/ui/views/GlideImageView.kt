package com.diegusmich.intouch.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.diegusmich.intouch.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.storage.StorageReference
import java.io.File

@SuppressLint("CheckResult")
class GlideImageView(ctx: Context, attrs: AttributeSet) : ShapeableImageView(ctx, attrs) {

    private val glideOptions: RequestOptions

    init {
        ctx.theme.obtainStyledAttributes(attrs, R.styleable.GlideImageView, 0, 0).apply {
            try {
                glideOptions = RequestOptions().apply {
                    placeholder(
                        getResourceId(
                            R.styleable.GlideImageView_placeholder,
                            R.drawable.glide_image_placeholder_blank
                        )
                    )
                    centerCrop()
                    diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                }
            } finally {
                recycle()
            }
        }
    }

    fun load(image: File){
        Glide.with(context).load(image).apply(glideOptions).into(this@GlideImageView)
    }

    fun load(url: String) {
        if (url.isNotBlank())
            Glide.with(context).load(url).apply(glideOptions).into(this@GlideImageView)
    }

    fun load(imgReference: StorageReference?) {
        Glide.with(context).load(imgReference).apply(glideOptions).into(this@GlideImageView)
    }

    fun clear() {
        Glide.with(context).clear(this)
    }
}