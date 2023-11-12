package com.diegusmich.intouch.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.diegusmich.intouch.R
import com.diegusmich.intouch.module.GlideApp
import com.diegusmich.intouch.module.GlideRequest
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("CheckResult")
class GlideImageView(private val ctx: Context, attrs: AttributeSet) : AppCompatImageView(ctx, attrs) {

    private val _glideRequest: GlideRequest<Drawable>
    private var _imgRef : String? = null
    val imgRef get() = _imgRef

    init {
        ctx.theme.obtainStyledAttributes(attrs, R.styleable.GlideImageView, 0, 0).apply {
            try {
                val isCircleCropped = getBoolean(R.styleable.GlideImageView_circleCrop, false)

                _glideRequest = GlideApp.with(ctx).load(this@GlideImageView.drawable).apply {
                    placeholder(R.drawable.glide_image_placeholder)

                    if (isCircleCropped)
                        circleCrop()

                    diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    into(this@GlideImageView)
                }
            } finally {
                recycle()
            }
        }
    }

    fun load(url: String) {
        _glideRequest.load(url).into(this)
    }

    fun load(imgReference: StorageReference) {
        _glideRequest.load(imgReference).into(this@GlideImageView)
    }

    fun clear(){
        GlideApp.with(ctx).clear(this)
    }
}